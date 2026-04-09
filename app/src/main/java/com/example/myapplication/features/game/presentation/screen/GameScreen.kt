package com.example.myapplication.features.game.presentation.screen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.domain.model.GameRoundState
import com.example.myapplication.features.game.presentation.state.GameUiState

@Composable
fun GameScreen(
    uiState: GameUiState,
    onMarkCurrentAsUsedAndNext: () -> Unit,
    onSkipCurrentAndNext: () -> Unit,
    onResetQuestions: () -> Unit,
    onAnswerChanged: (String) -> Unit,
    onReadyTapped: () -> Unit,
    onUndoReadyTapped: () -> Unit,
    onNextTapped: () -> Unit
) {
    val background = Color(0xFF24123F)
    val surface = Color(0xFF3B2066)
    val surfaceAlt = Color(0xFF4A2D7A)
    val accent = Color(0xFFB388FF)

    if (uiState.roundState == GameRoundState.Countdown) {
        val pulse = rememberInfiniteTransition(label = "revealPulse")
        val pulseScale by pulse.animateFloat(
            initialValue = 1f,
            targetValue = 1.18f,
            animationSpec = infiniteRepeatable(
                animation = tween(550),
                repeatMode = RepeatMode.Reverse
            ),
            label = "revealPulseScale"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "REVEAL",
                    color = Color(0xFF9BE7FF)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "${uiState.revealCountdownSecondsLeft}",
                    color = Color.White,
                    modifier = Modifier.scale(pulseScale)
                )
                Spacer(modifier = Modifier.height(20.dp))
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF4DD0E1), RoundedCornerShape(18.dp))
                        .padding(0.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF004D61)),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = uiState.currentQuestion,
                        color = Color(0xFFE0F7FA),
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        }
        return
    }

    val statusColor = when (uiState.roundState) {
        GameRoundState.Answering -> Color(0xFFE1BEE7)
        GameRoundState.ReadyPendingUndo -> Color(0xFFFFF59D)
        GameRoundState.Reveal -> Color(0xFFA5D6A7)
        GameRoundState.WaitingNext -> Color(0xFFB39DDB)
        GameRoundState.Idle -> Color(0xFFCFD8DC)
        GameRoundState.Countdown -> Color(0xFF80DEEA)
    }
    val statusText = when (uiState.roundState) {
        GameRoundState.Answering -> "Write your answer"
        GameRoundState.ReadyPendingUndo -> "Ready sent. You can undo"
        GameRoundState.Countdown -> "Reveal incoming"
        GameRoundState.Reveal -> "Answers revealed"
        GameRoundState.WaitingNext -> "Waiting next confirmation"
        GameRoundState.Idle -> "No questions available"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(statusText, color = statusColor) }
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, accent, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = surfaceAlt),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
                ) {
                    Text(
                        text = uiState.currentQuestion,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 22.dp)
                    )
                }
            }

            when (uiState.roundState) {
                GameRoundState.Answering -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = surface),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
                        ) {
                            TextField(
                                value = uiState.answerDraft,
                                onValueChange = onAnswerChanged,
                                placeholder = { Text("Write your answer...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF6A42A8),
                                    unfocusedContainerColor = Color(0xFF5A358F),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedPlaceholderColor = Color(0xFFD1C4E9),
                                    unfocusedPlaceholderColor = Color(0xFFD1C4E9)
                                )
                            )
                        }

                        Button(
                            onClick = onReadyTapped,
                            enabled = uiState.answerDraft.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E5DFF))
                        ) {
                            Text("Ready")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onSkipCurrentAndNext,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF607D8B)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Skip")
                            }
                            Button(
                                onClick = onMarkCurrentAsUsedAndNext,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Mark used")
                            }
                        }
                    }
                }

                GameRoundState.ReadyPendingUndo -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF6A1B9A)),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                        ) {
                            Text(
                                text = "Ready locked. Undo in ${uiState.undoSecondsLeft}s",
                                color = Color(0xFFF3E5F5),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Button(
                            onClick = onUndoReadyTapped,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAB47BC))
                        ) {
                            Text("Undo")
                        }
                    }
                }

                GameRoundState.Countdown -> Unit

                GameRoundState.Reveal,
                GameRoundState.WaitingNext -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.revealedAnswers.forEachIndexed { index, answer ->
                            val cardColor = if (index % 2 == 0) Color(0xFF4A2A76) else Color(0xFF37474F)
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = answer.userName,
                                        color = Color(0xFFD1C4E9)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = answer.answer,
                                        color = Color(0xFFF3E5F5)
                                    )
                                }
                            }
                        }

                        if (uiState.roundState == GameRoundState.Reveal) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = onNextTapped,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E5DFF))
                            ) {
                                Text("Next question")
                            }
                        } else {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF4527A0)),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                            ) {
                                Text(
                                    text = "Next confirmations: ${uiState.nextConfirmations}/${uiState.requiredNextConfirmations}",
                                    color = Color(0xFFEDE7F6),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp)
                                )
                            }
                            Button(
                                onClick = onNextTapped,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2))
                            ) {
                                Text("Confirm next")
                            }
                        }
                    }
                }

                GameRoundState.Idle -> {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF455A64)),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "No questions left. Reset to start again.",
                            color = Color(0xFFECEFF1),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }

            Button(
                onClick = onResetQuestions,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset questions")
            }
        }
    }
}
