package com.example.myapplication.core.domain.model

sealed interface GameRoundState {
    data object Idle : GameRoundState
    data object Answering : GameRoundState
    data object ReadyPendingUndo : GameRoundState
    data object Countdown : GameRoundState
    data object Reveal : GameRoundState
    data object WaitingNext : GameRoundState
}
