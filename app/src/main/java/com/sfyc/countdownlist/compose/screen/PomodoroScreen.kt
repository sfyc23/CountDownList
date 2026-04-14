package com.sfyc.countdownlist.compose.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sfyc.countdownlist.compose.component.CircularTimerProgress
import com.sfyc.countdownlist.compose.component.StatusChip
import com.sfyc.countdownlist.compose.component.TimerDisplay
import com.sfyc.countdownlist.compose.component.TimerDisplaySize
import com.sfyc.countdownlist.compose.component.TimerStatus
import com.sfyc.countdownlist.compose.theme.LocalTimerColors
import com.sfyc.countdownlist.compose.viewmodel.PomodoroPhase
import com.sfyc.countdownlist.compose.viewmodel.PomodoroViewModel

@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val timerColors = LocalTimerColors.current

    val progressColor = when (state.phase) {
        PomodoroPhase.Work -> timerColors.running
        PomodoroPhase.Rest -> MaterialTheme.colorScheme.tertiary
    }

    val progress = if (state.totalPhaseMillis > 0) {
        state.remainingMillis.toFloat() / state.totalPhaseMillis
    } else 0f

    val status = when {
        !state.isRunning -> TimerStatus.Idle
        state.isPaused -> TimerStatus.Paused
        state.phase == PomodoroPhase.Work -> TimerStatus.Running
        else -> TimerStatus.Running
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        PhaseIndicator(
            currentRound = state.currentRound,
            totalRounds = state.totalRounds,
            phase = state.phase,
            isRunning = state.isRunning,
        )

        Spacer(modifier = Modifier.height(24.dp))

        CircularTimerProgress(
            progress = progress,
            size = 220.dp,
            strokeWidth = 6.dp,
            progressColor = progressColor,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TimerDisplay(
                    millis = state.remainingMillis,
                    size = TimerDisplaySize.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusChip(status = status)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (state.phase) {
                PomodoroPhase.Work -> "专注中"
                PomodoroPhase.Rest -> "休息中"
            }.let { if (!state.isRunning) "就绪" else it },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "第 ${state.currentRound} 轮 / 共 ${state.totalRounds} 轮",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        val shape = RoundedCornerShape(12.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!state.isRunning) {
                Button(
                    onClick = { viewModel.start() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("开始")
                }
            } else if (state.isPaused) {
                Button(
                    onClick = { viewModel.resume() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("继续")
                }
                OutlinedButton(
                    onClick = { viewModel.stop() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("结束")
                }
            } else {
                FilledTonalButton(
                    onClick = { viewModel.pause() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("暂停")
                }
                OutlinedButton(
                    onClick = { viewModel.stop() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("结束")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "今日统计",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard("完成番茄", "${state.completedPomodoros}", Modifier.weight(1f))
            StatCard("专注时长", "${state.totalFocusMinutes}m", Modifier.weight(1f))
            val rate = if (state.totalRounds > 0) {
                (state.completedPomodoros * 100 / state.totalRounds).coerceAtMost(100)
            } else 0
            StatCard("完成率", "${rate}%", Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PhaseIndicator(
    currentRound: Int,
    totalRounds: Int,
    phase: PomodoroPhase,
    isRunning: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (round in 1..totalRounds) {
            val isWork = round < currentRound ||
                    (round == currentRound && phase == PomodoroPhase.Work && isRunning)
            val isRest = round < currentRound ||
                    (round == currentRound && phase == PomodoroPhase.Rest && isRunning)
            val isCurrent = round == currentRound && isRunning

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 6.dp),
            ) {
                val dotColor = when {
                    round < currentRound -> MaterialTheme.colorScheme.primary
                    isCurrent && phase == PomodoroPhase.Work -> MaterialTheme.colorScheme.primary
                    isCurrent && phase == PomodoroPhase.Rest -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.outlineVariant
                }
                androidx.compose.foundation.Canvas(modifier = Modifier.size(12.dp)) {
                    drawCircle(color = dotColor)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${round}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrent) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
