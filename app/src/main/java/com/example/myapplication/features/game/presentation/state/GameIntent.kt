package com.example.myapplication.features.game.presentation.state

sealed interface GameIntent {
    data class AnswerChanged(val value: String) : GameIntent
    data object ReadyTapped : GameIntent
    data object UndoReadyTapped : GameIntent
    data object CountdownFinished : GameIntent
    data object NextTapped : GameIntent
    data object MarkCurrentAsUsedAndNext : GameIntent
    data object SkipCurrentAndNext : GameIntent
    data object ResetTapped : GameIntent
}
