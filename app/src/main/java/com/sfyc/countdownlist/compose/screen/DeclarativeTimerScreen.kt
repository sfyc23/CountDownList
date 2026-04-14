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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sfyc.countdownlist.compose.component.CircularTimerProgress
import com.sfyc.countdownlist.compose.component.StatusChip
import com.sfyc.countdownlist.compose.component.TimerDisplay
import com.sfyc.countdownlist.compose.component.TimerDisplaySize
import com.sfyc.countdownlist.compose.component.TimerStatus
import com.sfyc.countdownlist.compose.theme.LocalTimerColors
import kotlinx.coroutines.delay

/**
 * 纯声明式倒计时 — 完全由 Composable 自己驱动，不依赖 ViewModel。
 * 展示 LaunchedEffect + rememberSaveable 的正确用法。
 */
@Composable
fun DeclarativeTimerScreen() {
    val presets = listOf(30, 60, 120, 300)
    var selectedPreset by rememberSaveable { mutableIntStateOf(1) }
    val totalSeconds = presets[selectedPreset]

    var remaining by rememberSaveable { mutableIntStateOf(totalSeconds) }
    var isRunning by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isRunning, remaining) {
        if (isRunning && remaining > 0) {
            delay(1000L)
            remaining--
        } else if (remaining <= 0) {
            isRunning = false
        }
    }

    val progress = if (totalSeconds > 0) remaining.toFloat() / totalSeconds else 0f
    val timerColors = LocalTimerColors.current
    val progressColor = when {
        !isRunning && remaining == totalSeconds -> MaterialTheme.colorScheme.outlineVariant
        remaining <= 10 && remaining > 0 -> timerColors.warning
        remaining <= 0 -> timerColors.finished
        else -> timerColors.running
    }
    val status = when {
        !isRunning && remaining == totalSeconds -> TimerStatus.Idle
        !isRunning && remaining <= 0 -> TimerStatus.Finished
        !isRunning -> TimerStatus.Paused
        remaining <= 10 -> TimerStatus.Warning
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
                Text(
                    text = "纯声明式倒计时",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "无 ViewModel，仅用 LaunchedEffect + rememberSaveable 驱动。" +
                            "配置变更（如屏幕旋转）后状态自动恢复。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            presets.forEachIndexed { i, sec ->
                FilterChip(
                    selected = selectedPreset == i,
                    onClick = {
                        if (!isRunning) {
                            selectedPreset = i
                            remaining = sec
                        }
                    },
                    label = { Text("${sec}s") },
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
                    millis = remaining * 1000L,
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
            if (!isRunning) {
                Button(
                    onClick = {
                        if (remaining <= 0) remaining = totalSeconds
                        isRunning = true
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (remaining in 1 until totalSeconds) "继续" else "开始")
                }
            } else {
                Button(
                    onClick = { isRunning = false },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                ) {
                    Icon(Icons.Default.Pause, null)
                    Spacer(Modifier.width(8.dp))
                    Text("暂停")
                }
            }
            OutlinedButton(
                onClick = {
                    isRunning = false
                    remaining = totalSeconds
                },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = shape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Icon(Icons.Default.Stop, null)
                Spacer(Modifier.width(8.dp))
                Text("重置")
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "实现原理",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "• LaunchedEffect(isRunning, remaining) 监听状态变化\n" +
                            "• 每秒 delay(1000) 后 remaining-- 触发重组\n" +
                            "• rememberSaveable 保证配置变更后状态恢复\n" +
                            "• 适合简单场景，复杂逻辑建议使用 ViewModel",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
