package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.myapplication.features.game.presentation.screen.GameScreen
import com.example.myapplication.features.game.presentation.vm.GameViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by gameViewModel.uiState.collectAsState()

            MyApplicationTheme {
                GameScreen(
                    uiState = uiState,
                    onMarkCurrentAsUsedAndNext = gameViewModel::onMarkCurrentAsUsedAndNext,
                    onSkipCurrentAndNext = gameViewModel::onSkipCurrentAndNext,
                    onResetQuestions = gameViewModel::onResetQuestions,
                    onAnswerChanged = gameViewModel::onAnswerChanged,
                    onReadyTapped = gameViewModel::onReadyTapped,
                    onUndoReadyTapped = gameViewModel::onUndoReadyTapped,
                    onNextTapped = gameViewModel::onNextTapped
                )
            }
        }
    }
}
