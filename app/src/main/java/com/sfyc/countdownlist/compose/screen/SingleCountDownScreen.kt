package com.sfyc.countdownlist.compose.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sfyc.countdownlist.compose.component.CircularTimerProgress
import com.sfyc.countdownlist.compose.component.ControlButtonRow
import com.sfyc.countdownlist.compose.component.StatusChip
import com.sfyc.countdownlist.compose.component.TimerDisplay
import com.sfyc.countdownlist.compose.component.TimerDisplaySize
import com.sfyc.countdownlist.compose.component.TimerPhase
import com.sfyc.countdownlist.compose.component.TimerStatus
import com.sfyc.countdownlist.compose.theme.LocalTimerColors
import com.sfyc.countdownlist.compose.viewmodel.SingleCountDownViewModel

private val QuickDurations = listOf(30L, 60L, 120L, 180L, 300L)

@Composable
fun SingleCountDownScreen(
    viewModel: SingleCountDownViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val timerColors = LocalTimerColors.current

    val progress = if (state.totalMillis > 0) {
        state.remainingMillis.toFloat() / state.totalMillis
    } else 0f

    val phase = when {
        state.isFinished -> TimerPhase.Finished
        state.isPaused -> TimerPhase.Paused
        state.isRunning -> TimerPhase.Running
        else -> TimerPhase.Idle
    }

    val status = when {
        state.isFinished -> TimerStatus.Finished
        state.isPaused -> TimerStatus.Paused
        state.isRunning && state.remainingMillis < 10_000 -> TimerStatus.Warning
        state.isRunning -> TimerStatus.Running
        else -> TimerStatus.Idle
    }

    val progressColor = when {
        state.isFinished -> timerColors.finished
        state.remainingMillis < 10_000 && state.isRunning -> timerColors.warning
        state.isPaused -> timerColors.paused
        else -> timerColors.running
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        StatusChip(status = status)

        Spacer(modifier = Modifier.height(32.dp))

        CircularTimerProgress(
            progress = progress,
            size = 240.dp,
            strokeWidth = 6.dp,
            progressColor = progressColor,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TimerDisplay(
                    millis = state.remainingMillis,
                    size = TimerDisplaySize.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (state.isFinished) {
                    Text(
                        text = "时间到!",
                        color = timerColors.warning,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!state.isRunning) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                QuickDurations.forEach { sec ->
                    val selected = state.totalMillis / 1000 == sec
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.setDuration(sec) },
                        label = {
                            Text(if (sec >= 60) "${sec / 60}分" else "${sec}秒")
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "时长: ${state.totalMillis / 1000} 秒",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Slider(
                value = (state.totalMillis / 1000).toFloat(),
                onValueChange = { viewModel.setDuration(it.toLong()) },
                valueRange = 5f..300f,
                steps = 58,
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        ControlButtonRow(
            phase = phase,
            onStart = { viewModel.start() },
            onPause = { viewModel.pause() },
            onResume = { viewModel.resume() },
            onCancel = { viewModel.cancel() },
        )
    }
}
