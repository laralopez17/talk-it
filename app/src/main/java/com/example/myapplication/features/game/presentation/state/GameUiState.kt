package com.example.myapplication.features.game.presentation.state

import com.example.myapplication.core.domain.model.GameRoundState
import com.example.myapplication.core.domain.model.Question

data class GameUiState(
    val questions: List<Question> = emptyList(),
    val currentQuestion: String = "",
    val nextQuestionPreview: String = "",
    val answerDraft: String = "",
    val roundState: GameRoundState = GameRoundState.Idle,
    val undoSecondsLeft: Int = 0,
    val revealCountdownSecondsLeft: Int = 0,
    val nextConfirmations: Int = 0,
    val requiredNextConfirmations: Int = 2,
    val revealedAnswers: List<RevealedAnswer> = emptyList()
)
