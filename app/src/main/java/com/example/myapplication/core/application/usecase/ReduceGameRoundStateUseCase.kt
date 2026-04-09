package com.example.myapplication.core.application.usecase

import com.example.myapplication.core.domain.model.GameRoundState
import com.example.myapplication.features.game.presentation.state.GameIntent

class ReduceGameRoundStateUseCase {
    operator fun invoke(
        currentState: GameRoundState,
        intent: GameIntent
    ): GameRoundState {
        return when (intent) {
            is GameIntent.AnswerChanged -> {
                when (currentState) {
                    GameRoundState.Idle -> GameRoundState.Answering
                    GameRoundState.Answering -> GameRoundState.Answering
                    else -> currentState
                }
            }

            GameIntent.ReadyTapped -> {
                when (currentState) {
                    GameRoundState.Answering -> GameRoundState.ReadyPendingUndo
                    else -> currentState
                }
            }

            GameIntent.UndoReadyTapped -> {
                when (currentState) {
                    GameRoundState.ReadyPendingUndo -> GameRoundState.Answering
                    else -> currentState
                }
            }

            GameIntent.CountdownFinished -> {
                when (currentState) {
                    GameRoundState.ReadyPendingUndo -> GameRoundState.Countdown
                    GameRoundState.Countdown -> GameRoundState.Reveal
                    else -> currentState
                }
            }

            GameIntent.NextTapped -> {
                when (currentState) {
                    GameRoundState.Reveal -> GameRoundState.WaitingNext
                    GameRoundState.WaitingNext -> GameRoundState.WaitingNext
                    else -> currentState
                }
            }

            GameIntent.MarkCurrentAsUsedAndNext -> {
                when (currentState) {
                    GameRoundState.Idle,
                    GameRoundState.Answering -> GameRoundState.Answering

                    else -> currentState
                }
            }

            GameIntent.SkipCurrentAndNext -> {
                when (currentState) {
                    GameRoundState.Idle,
                    GameRoundState.Answering -> GameRoundState.Answering

                    else -> currentState
                }
            }

            GameIntent.ResetTapped -> GameRoundState.Answering
        }
    }
}
