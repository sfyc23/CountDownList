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
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sfyc.countdownlist.compose.component.TimerDisplay
import com.sfyc.countdownlist.compose.component.TimerDisplaySize
import com.sfyc.countdownlist.compose.theme.LocalTimerColors
import com.sfyc.countdownlist.compose.viewmodel.ServerSyncViewModel

@Composable
fun ServerSyncScreen(
    viewModel: ServerSyncViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val timerColors = LocalTimerColors.current
    val shape = RoundedCornerShape(12.dp)

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
                    Icon(Icons.Default.CloudSync, null, tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "服务端时间校准",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "模拟客户端与服务端时钟偏移。对比「无校准」vs「有校准」的倒计时差异，体现 NTP/HTTP 对时的必要性。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        FilledTonalButton(
            onClick = { viewModel.syncTime() },
            shape = shape,
            enabled = !state.isRunning,
        ) {
            Icon(Icons.Default.Sync, null)
            Spacer(Modifier.width(8.dp))
            Text("模拟对时")
        }

        if (state.isSynced) {
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow("偏移量", "${state.offsetMs} ms")
                    InfoRow("本地时间", formatTimestamp(state.localTimeMs))
                    InfoRow("服务端时间", formatTimestamp(state.serverTimeMs))
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (state.isRunning) {
            Text(
                text = "无校准（本地时间直接算）",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
            )
            TimerDisplay(
                millis = state.remainingRaw,
                size = TimerDisplaySize.Medium,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
            )

            Spacer(Modifier.height(8.dp))
            Divider(Modifier.padding(horizontal = 40.dp))
            Spacer(Modifier.height(8.dp))

            Text(
                text = "有校准（加上偏移量修正）",
                style = MaterialTheme.typography.labelMedium,
                color = timerColors.running,
            )
            TimerDisplay(
                millis = state.remainingSynced,
                size = TimerDisplaySize.Large,
                color = timerColors.running,
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "差异 ≈ ${kotlin.math.abs(state.remainingRaw - state.remainingSynced)} ms",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!state.isRunning) {
                Button(
                    onClick = { viewModel.start() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                    enabled = state.isSynced,
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text("30 秒倒计时")
                }
            } else {
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

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
    }
}

private fun formatTimestamp(ms: Long): String {
    val sdf = java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(ms))
}
