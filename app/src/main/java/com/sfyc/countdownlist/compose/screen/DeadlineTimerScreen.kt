package com.sfyc.countdownlist.compose.screen

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.sfyc.countdownlist.compose.viewmodel.DeadlineTimerViewModel

@Composable
fun DeadlineTimerScreen(
    viewModel: DeadlineTimerViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val timerColors = LocalTimerColors.current

    val presets = listOf(30_000L, 60_000L, 120_000L, 300_000L)
    val presetLabels = listOf("30s", "1m", "2m", "5m")
    var selectedPreset by rememberSaveable { mutableIntStateOf(1) }

    val progress = if (state.totalMs > 0) state.remainingMs.toFloat() / state.totalMs else 0f
    val progressColor = when {
        !state.isRunning && state.remainingMs <= 0L -> timerColors.finished
        state.isPaused -> timerColors.paused
        state.remainingMs <= 10_000L && state.isRunning -> timerColors.warning
        state.isRunning -> timerColors.running
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val status = when {
        !state.isRunning && state.remainingMs <= 0L && state.totalMs > 0 -> TimerStatus.Finished
        state.isPaused -> TimerStatus.Paused
        state.isRunning && state.remainingMs <= 10_000L -> TimerStatus.Warning
        state.isRunning -> TimerStatus.Running
        else -> TimerStatus.Idle
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
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Deadline 模型",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "只持久化 deadline 时刻，界面用 deadline - now 反推剩余时间。" +
                            "页面重建、进后台回来都不会累积误差。配合 SavedStateHandle 做进程恢复。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            presets.forEachIndexed { i, ms ->
                FilterChip(
                    selected = selectedPreset == i,
                    onClick = {
                        if (!state.isRunning) {
                            selectedPreset = i
                            viewModel.setDuration(ms)
                        }
                    },
                    label = { Text(presetLabels[i]) },
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        CircularTimerProgress(
            progress = progress,
            size = 220.dp,
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

        Spacer(Modifier.height(24.dp))

        val shape = RoundedCornerShape(12.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when {
                !state.isRunning -> {
                    Button(
                        onClick = {
                            if (state.remainingMs <= 0L) viewModel.setDuration(presets[selectedPreset])
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
                        onClick = { viewModel.cancel() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = shape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    ) {
                        Icon(Icons.Default.Stop, null)
                        Spacer(Modifier.width(8.dp))
                        Text("取消")
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
                        onClick = { viewModel.cancel() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = shape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    ) {
                        Icon(Icons.Default.Stop, null)
                        Spacer(Modifier.width(8.dp))
                        Text("取消")
                    }
                }
            }
        }
    }
}
