package com.sfyc.countdownlist.compose.screen

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sfyc.countdownlist.compose.component.CircularTimerProgress
import com.sfyc.countdownlist.compose.component.StatusChip
import com.sfyc.countdownlist.compose.component.TimerDisplay
import com.sfyc.countdownlist.compose.component.TimerDisplaySize
import com.sfyc.countdownlist.compose.component.TimerStatus
import com.sfyc.countdownlist.compose.theme.LocalTimerColors
import com.sfyc.countdownlist.compose.viewmodel.RepeatTimerViewModel

@Composable
fun RepeatTimerScreen(
    viewModel: RepeatTimerViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val timerColors = LocalTimerColors.current
    val context = LocalContext.current

    val durations = listOf(10, 30, 60)
    val durationLabels = listOf("10s", "30s", "1m")
    var selectedDuration by rememberSaveable { mutableIntStateOf(1) }

    val rounds = listOf(3, 5, 10)
    val roundLabels = listOf("3轮", "5轮", "10轮")
    var selectedRound by rememberSaveable { mutableIntStateOf(1) }

    LaunchedEffect(state.roundJustFinished) {
        if (state.roundJustFinished) {
            triggerVibrate(context)
            viewModel.clearRoundFinished()
        }
    }

    val totalMs = state.durationSeconds * 1000L
    val progress = if (totalMs > 0) state.remainingMs.toFloat() / totalMs else 0f
    val progressColor = when {
        !state.isRunning -> MaterialTheme.colorScheme.outlineVariant
        state.isPaused -> timerColors.paused
        state.remainingMs <= 3_000L -> timerColors.warning
        else -> timerColors.running
    }
    val status = when {
        !state.isRunning && state.currentRound == 0 -> TimerStatus.Idle
        !state.isRunning -> TimerStatus.Finished
        state.isPaused -> TimerStatus.Paused
        state.remainingMs <= 3_000L -> TimerStatus.Warning
        else -> TimerStatus.Running
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Repeat, null, tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "循环倒计时",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "倒计时结束后自动重新开始，每轮结束触发震动反馈。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text("时长", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            durations.forEachIndexed { i, _ ->
                FilterChip(
                    selected = selectedDuration == i,
                    onClick = {
                        if (!state.isRunning) {
                            selectedDuration = i
                            viewModel.setConfig(durations[i], rounds[selectedRound], false)
                        }
                    },
                    label = { Text(durationLabels[i]) },
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text("轮次", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            rounds.forEachIndexed { i, _ ->
                FilterChip(
                    selected = selectedRound == i,
                    onClick = {
                        if (!state.isRunning) {
                            selectedRound = i
                            viewModel.setConfig(durations[selectedDuration], rounds[i], false)
                        }
                    },
                    label = { Text(roundLabels[i]) },
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        CircularTimerProgress(
            progress = progress,
            size = 200.dp,
            strokeWidth = 6.dp,
            progressColor = progressColor,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TimerDisplay(
                    millis = state.remainingMs,
                    size = TimerDisplaySize.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                StatusChip(status = status)
            }
        }

        Spacer(Modifier.height(8.dp))

        if (state.isRunning || state.currentRound > 0) {
            val roundText = if (state.isInfinite) "第 ${state.currentRound} 轮 (无限)"
            else "第 ${state.currentRound} 轮 / 共 ${state.totalRounds} 轮"
            Text(
                text = roundText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(20.dp))

        val shape = RoundedCornerShape(12.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when {
                !state.isRunning -> {
                    Button(
                        onClick = {
                            viewModel.setConfig(durations[selectedDuration], rounds[selectedRound], false)
                            viewModel.start()
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = shape,
                    ) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(8.dp))
                        Text("开始")
                    }
                }
                state.isPaused -> {
                    Button(
                        onClick = { viewModel.resume() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = shape,
                    ) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(8.dp))
                        Text("继续")
                    }
                    OutlinedButton(
                        onClick = { viewModel.stop() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = shape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    ) {
                        Icon(Icons.Default.Stop, null)
                        Spacer(Modifier.width(8.dp))
                        Text("停止")
                    }
                }
                else -> {
                    FilledTonalButton(
                        onClick = { viewModel.pause() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = shape,
                    ) {
                        Icon(Icons.Default.Pause, null)
                        Spacer(Modifier.width(8.dp))
                        Text("暂停")
                    }
                    OutlinedButton(
                        onClick = { viewModel.stop() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = shape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    ) {
                        Icon(Icons.Default.Stop, null)
                        Spacer(Modifier.width(8.dp))
                        Text("停止")
                    }
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
private fun triggerVibrate(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val mgr = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        mgr?.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    vibrator?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            it.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            it.vibrate(200)
        }
    }
}
