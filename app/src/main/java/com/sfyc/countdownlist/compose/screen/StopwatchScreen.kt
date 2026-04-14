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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sfyc.countdownlist.compose.component.CircularTimerProgress
import com.sfyc.countdownlist.compose.component.TimerDisplay
import com.sfyc.countdownlist.compose.component.TimerDisplaySize
import com.sfyc.countdownlist.compose.component.formatTimerText
import com.sfyc.countdownlist.compose.theme.LocalTimerColors
import com.sfyc.countdownlist.compose.viewmodel.LapRecord
import com.sfyc.countdownlist.compose.viewmodel.StopwatchViewModel

@Composable
fun StopwatchScreen(
    viewModel: StopwatchViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val timerColors = LocalTimerColors.current

    val loopProgress = if (state.isRunning || state.elapsedMillis > 0) {
        ((state.elapsedMillis % 60_000L) / 60_000f)
    } else 0f

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        CircularTimerProgress(
            progress = loopProgress,
            size = 220.dp,
            strokeWidth = 5.dp,
            progressColor = timerColors.stopwatch,
        ) {
            TimerDisplay(
                millis = state.elapsedMillis,
                size = TimerDisplaySize.Medium,
                showMillis = true,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val shape = RoundedCornerShape(12.dp)

            if (state.isRunning) {
                FilledTonalButton(
                    onClick = { viewModel.lap() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                ) {
                    Icon(Icons.Default.Flag, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("分段")
                }
                Button(
                    onClick = { viewModel.pause() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = timerColors.stopwatch,
                    ),
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("暂停")
                }
            } else if (state.elapsedMillis > 0) {
                OutlinedButton(
                    onClick = { viewModel.reset() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("重置")
                }
                Button(
                    onClick = { viewModel.startOrResume() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = timerColors.stopwatch,
                    ),
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("继续")
                }
            } else {
                Button(
                    onClick = { viewModel.startOrResume() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = shape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = timerColors.stopwatch,
                    ),
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("开始")
                }
            }
        }

        if (state.laps.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            val fastestIdx = remember(state.laps) {
                state.laps.minByOrNull { it.splitMillis }?.index
            }
            val slowestIdx = remember(state.laps) {
                if (state.laps.size > 1) state.laps.maxByOrNull { it.splitMillis }?.index else null
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("分段", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("分段时间", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("总时间", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(state.laps.reversed()) { lap ->
                    LapRow(
                        lap = lap,
                        isFastest = lap.index == fastestIdx,
                        isSlowest = lap.index == slowestIdx,
                    )
                }
            }
        }
    }
}

@Composable
private fun LapRow(
    lap: LapRecord,
    isFastest: Boolean,
    isSlowest: Boolean,
) {
    val timerColors = LocalTimerColors.current
    val color = when {
        isFastest -> timerColors.stopwatch
        isSlowest -> timerColors.warning
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "#${lap.index}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = color,
        )
        Text(
            text = formatTimerText(lap.splitMillis, showMillis = true),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = color,
        )
        Text(
            text = formatTimerText(lap.totalMillis, showMillis = true),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
