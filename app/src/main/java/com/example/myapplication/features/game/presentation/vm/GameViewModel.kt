package com.example.myapplication.features.game.presentation.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.application.usecase.ClearUsedQuestionIdsUseCase
import com.example.myapplication.core.application.usecase.GetQuestionsUseCase
import com.example.myapplication.core.application.usecase.ReduceGameRoundStateUseCase
import com.example.myapplication.core.application.usecase.SaveUsedQuestionIdsUseCase
import com.example.myapplication.core.domain.model.GameRoundState
import com.example.myapplication.core.domain.model.Question
import com.example.myapplication.data.local.SharedPreferencesQuestionRepository
import com.example.myapplication.features.game.presentation.state.GameIntent
import com.example.myapplication.features.game.presentation.state.GameUiState
import com.example.myapplication.features.game.presentation.state.RevealedAnswer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = SharedPreferencesQuestionRepository(application.applicationContext)
    private val getQuestionsUseCase = GetQuestionsUseCase(repository)
    private val saveUsedQuestionIdsUseCase = SaveUsedQuestionIdsUseCase(repository)
    private val clearUsedQuestionIdsUseCase = ClearUsedQuestionIdsUseCase(repository)
    private val reduceGameRoundStateUseCase = ReduceGameRoundStateUseCase()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    private var undoJob: Job? = null
    private var revealCountdownJob: Job? = null

    init {
        val questions = getQuestionsUseCase()
        val persistedCurrentQuestion = savedStateHandle.get<String>(KEY_CURRENT_QUESTION)
        val persistedAnswerDraft = savedStateHandle.get<String>(KEY_ANSWER_DRAFT).orEmpty()
        val persistedRoundStateName = savedStateHandle.get<String>(KEY_ROUND_STATE)
        val persistedRoundState = parseRoundState(persistedRoundStateName)

        val fallbackCurrent = questions.filter { !it.used }.randomOrNull()?.text
            ?: questions.randomOrNull()?.text
            ?: "No hay preguntas"

        val current = persistedCurrentQuestion ?: fallbackCurrent
        val remaining = questions.filter { it.text != current && !it.used }
        val next = remaining.randomOrNull()?.text ?: current

        _uiState.value = GameUiState(
            questions = questions,
            currentQuestion = current,
            nextQuestionPreview = next,
            answerDraft = persistedAnswerDraft,
            roundState = persistedRoundState ?: GameRoundState.Answering
        )
    }

    fun onIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.AnswerChanged -> {
                val previous = _uiState.value.roundState
                transitionRound(intent)
                if (_uiState.value.roundState == GameRoundState.Answering || previous == GameRoundState.Answering) {
                    updateUiState(_uiState.value.copy(answerDraft = intent.value))
                }
            }

            GameIntent.ReadyTapped -> {
                if (_uiState.value.answerDraft.isBlank()) return
                val previous = _uiState.value.roundState
                transitionRound(intent)
                if (previous != _uiState.value.roundState && _uiState.value.roundState == GameRoundState.ReadyPendingUndo) {
                    startUndoWindow()
                }
            }

            GameIntent.UndoReadyTapped -> {
                val previous = _uiState.value.roundState
                transitionRound(intent)
                if (previous != _uiState.value.roundState && _uiState.value.roundState == GameRoundState.Answering) {
                    stopUndoWindow()
                }
            }

            GameIntent.CountdownFinished -> {
                transitionRound(intent)
                if (_uiState.value.roundState == GameRoundState.Countdown) {
                    startRevealCountdown()
                } else if (_uiState.value.roundState == GameRoundState.Reveal) {
                    updateUiState(
                        _uiState.value.copy(
                            revealCountdownSecondsLeft = 0,
                            revealedAnswers = buildRevealedAnswers(_uiState.value.answerDraft)
                        )
                    )
                }
            }

            GameIntent.NextTapped -> {
                val previous = _uiState.value.roundState
                transitionRound(intent)
                if (previous == GameRoundState.Reveal && _uiState.value.roundState == GameRoundState.WaitingNext) {
                    updateUiState(_uiState.value.copy(nextConfirmations = 1))
                    return
                }
                if (_uiState.value.roundState == GameRoundState.WaitingNext) {
                    val updatedConfirmations = (_uiState.value.nextConfirmations + 1)
                        .coerceAtMost(_uiState.value.requiredNextConfirmations)
                    if (updatedConfirmations >= _uiState.value.requiredNextConfirmations) {
                        advanceToNextQuestion(_uiState.value.questions)
                    } else {
                        updateUiState(_uiState.value.copy(nextConfirmations = updatedConfirmations))
                    }
                }
            }

            GameIntent.MarkCurrentAsUsedAndNext -> {
                if (!canSwipeCurrentState()) return
                transitionRound(intent)
                markCurrentAsUsedAndNext()
            }

            GameIntent.SkipCurrentAndNext -> {
                if (!canSwipeCurrentState()) return
                transitionRound(intent)
                advanceToNextQuestion(_uiState.value.questions)
            }

            GameIntent.ResetTapped -> {
                stopUndoWindow()
                stopRevealCountdown()
                transitionRound(intent)
                clearUsedQuestionIdsUseCase()
                val resetQuestions = _uiState.value.questions.map { it.copy(used = false) }
                advanceToNextQuestion(resetQuestions)
            }
        }
    }

    fun onMarkCurrentAsUsedAndNext() {
        onIntent(GameIntent.MarkCurrentAsUsedAndNext)
    }

    fun onSkipCurrentAndNext() {
        onIntent(GameIntent.SkipCurrentAndNext)
    }

    fun onResetQuestions() {
        onIntent(GameIntent.ResetTapped)
    }

    fun onAnswerChanged(value: String) {
        onIntent(GameIntent.AnswerChanged(value))
    }

    fun onReadyTapped() {
        onIntent(GameIntent.ReadyTapped)
    }

    fun onUndoReadyTapped() {
        onIntent(GameIntent.UndoReadyTapped)
    }

    fun onNextTapped() {
        onIntent(GameIntent.NextTapped)
    }

    private fun markCurrentAsUsedAndNext() {
        val currentState = _uiState.value
        val index = currentState.questions.indexOfFirst { it.text == currentState.currentQuestion }
        if (index == -1) return

        val updatedQuestions = currentState.questions.toMutableList().apply {
            this[index] = this[index].copy(used = true)
        }

        saveUsedQuestionIdsUseCase(updatedQuestions.filter { it.used }.map { it.id }.toSet())
        advanceToNextQuestion(updatedQuestions)
    }

    private fun advanceToNextQuestion(questions: List<Question>) {
        stopUndoWindow()
        stopRevealCountdown()
        val available = questions.filter { !it.used }
        if (available.isEmpty()) {
            updateUiState(
                _uiState.value.copy(
                    questions = questions,
                    currentQuestion = "No quedan preguntas 😅",
                    nextQuestionPreview = "",
                    answerDraft = "",
                    roundState = GameRoundState.Idle,
                    undoSecondsLeft = 0,
                    revealCountdownSecondsLeft = 0,
                    nextConfirmations = 0,
                    revealedAnswers = emptyList()
                )
            )
            return
        }

        val newQuestion = available.random().text
        val remaining = available.filter { it.text != newQuestion }
        val nextPreview = remaining.randomOrNull()?.text ?: newQuestion

        updateUiState(
            _uiState.value.copy(
                questions = questions,
                currentQuestion = newQuestion,
                nextQuestionPreview = nextPreview,
                answerDraft = "",
                roundState = GameRoundState.Answering,
                undoSecondsLeft = 0,
                revealCountdownSecondsLeft = 0,
                nextConfirmations = 0,
                revealedAnswers = emptyList()
            )
        )
    }

    private fun transitionRound(intent: GameIntent) {
        val current = _uiState.value.roundState
        val next = reduceGameRoundStateUseCase(current, intent)
        if (current == next) return
        updateUiState(_uiState.value.copy(roundState = next))
    }

    private fun canSwipeCurrentState(): Boolean {
        return _uiState.value.roundState == GameRoundState.Answering ||
            _uiState.value.roundState == GameRoundState.Idle
    }

    private fun startUndoWindow() {
        stopUndoWindow()
        undoJob = viewModelScope.launch {
            for (seconds in UNDO_WINDOW_SECONDS downTo 1) {
                updateUiState(_uiState.value.copy(undoSecondsLeft = seconds))
                delay(1000)
            }
            updateUiState(_uiState.value.copy(undoSecondsLeft = 0))
            onIntent(GameIntent.CountdownFinished)
        }
    }

    private fun stopUndoWindow() {
        undoJob?.cancel()
        undoJob = null
        if (_uiState.value.undoSecondsLeft != 0) {
            updateUiState(_uiState.value.copy(undoSecondsLeft = 0))
        }
    }

    private fun startRevealCountdown() {
        stopRevealCountdown()
        revealCountdownJob = viewModelScope.launch {
            for (seconds in REVEAL_COUNTDOWN_SECONDS downTo 1) {
                updateUiState(_uiState.value.copy(revealCountdownSecondsLeft = seconds))
                delay(1000)
            }
            updateUiState(_uiState.value.copy(revealCountdownSecondsLeft = 0))
            onIntent(GameIntent.CountdownFinished)
        }
    }

    private fun stopRevealCountdown() {
        revealCountdownJob?.cancel()
        revealCountdownJob = null
        if (_uiState.value.revealCountdownSecondsLeft != 0) {
            updateUiState(_uiState.value.copy(revealCountdownSecondsLeft = 0))
        }
    }

    private fun updateUiState(newState: GameUiState) {
        _uiState.value = newState
        savedStateHandle[KEY_CURRENT_QUESTION] = newState.currentQuestion
        savedStateHandle[KEY_ANSWER_DRAFT] = newState.answerDraft
        savedStateHandle[KEY_ROUND_STATE] = newState.roundState::class.simpleName ?: "Answering"
    }

    private fun parseRoundState(value: String?): GameRoundState? {
        return when (value) {
            "Idle" -> GameRoundState.Idle
            "Answering" -> GameRoundState.Answering
            "ReadyPendingUndo" -> GameRoundState.ReadyPendingUndo
            "Countdown" -> GameRoundState.Countdown
            "Reveal" -> GameRoundState.Reveal
            "WaitingNext" -> GameRoundState.WaitingNext
            else -> null
        }
    }

    private fun buildRevealedAnswers(userAnswer: String): List<RevealedAnswer> {
        val normalizedUserAnswer = userAnswer.ifBlank { "Sin respuesta" }
        val mockPartnerAnswer = MOCK_PARTNER_ANSWERS.random()
        return listOf(
            RevealedAnswer(
                userName = "User1",
                answer = normalizedUserAnswer
            ),
            RevealedAnswer(
                userName = "User2",
                answer = mockPartnerAnswer
            )
        )
    }

    companion object {
        private const val KEY_CURRENT_QUESTION = "game.currentQuestion"
        private const val KEY_ANSWER_DRAFT = "game.answerDraft"
        private const val KEY_ROUND_STATE = "game.roundState"
        private const val UNDO_WINDOW_SECONDS = 3
        private const val REVEAL_COUNTDOWN_SECONDS = 3
        private val MOCK_PARTNER_ANSWERS = listOf(
            "Me encanta esa idea.",
            "Diria que si, sin pensarlo.",
            "Me hizo acordar a un momento lindo.",
            "Elegiria algo simple y divertido.",
            "Totalmente de acuerdo con vos."
        )
    }
}
