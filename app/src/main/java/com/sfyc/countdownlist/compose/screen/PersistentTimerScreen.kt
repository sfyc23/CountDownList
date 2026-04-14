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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sfyc.countdownlist.compose.theme.LocalTimerColors
import com.sfyc.countdownlist.compose.theme.TimerFontFamily
import com.sfyc.countdownlist.compose.viewmodel.PersistentTimerViewModel
import com.sfyc.countdownlist.data.TimerEntity

@Composable
fun PersistentTimerScreen(
    viewModel: PersistentTimerViewModel = viewModel(),
) {
    val timers by viewModel.allTimers.collectAsState()
    val now by viewModel.nowRealtime.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val names = listOf("训练倒计时", "烹饪计时", "专注时间", "休息提醒", "会议倒计时")
                    val durations = listOf(30_000L, 60_000L, 90_000L, 120_000L, 180_000L)
                    val idx = (System.currentTimeMillis() % names.size).toInt()
                    viewModel.addTimer(names[idx], durations[idx])
                },
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "端到端持久化 Demo",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "创建 → Room 持久化 → Flow 实时展示 → WorkManager 到点通知。" +
                                "App 被杀后任务仍会按时触发通知。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (timers.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "暂无任务",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "点击右下角 + 添加倒计时任务",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(timers, key = { it.id }) { timer ->
                        TimerTaskCard(
                            timer = timer,
                            now = now,
                            onCancel = { viewModel.cancelTimer(timer) },
                            onDelete = { viewModel.deleteTimer(timer) },
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun TimerTaskCard(
    timer: TimerEntity,
    now: Long,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
) {
    val timerColors = LocalTimerColors.current

    val remainingMs = if (timer.status == TimerEntity.STATUS_RUNNING) {
        (timer.deadlineMs - System.currentTimeMillis()).coerceAtLeast(0)
    } else 0L

    val statusText: String
    val statusColor: androidx.compose.ui.graphics.Color

    when (timer.status) {
        TimerEntity.STATUS_RUNNING -> {
            if (remainingMs <= 0) {
                statusText = "已完成"
                statusColor = timerColors.finished
            } else if (remainingMs <= 10_000) {
                statusText = "即将结束"
                statusColor = timerColors.warning
            } else {
                statusText = "运行中"
                statusColor = timerColors.running
            }
        }
        TimerEntity.STATUS_FINISHED -> {
            statusText = "已完成"
            statusColor = timerColors.finished
        }
        TimerEntity.STATUS_CANCELLED -> {
            statusText = "已取消"
            statusColor = MaterialTheme.colorScheme.outline
        }
        else -> {
            statusText = "等待中"
            statusColor = MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

    val totalSec = (remainingMs / 1000).toInt()
    val timeText = if (timer.status == TimerEntity.STATUS_RUNNING && remainingMs > 0) {
        String.format("%02d:%02d", totalSec / 60, totalSec % 60)
    } else {
        "--:--"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = timer.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                )
            }
            Text(
                text = timeText,
                fontFamily = TimerFontFamily,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = statusColor,
            )
            Spacer(Modifier.width(8.dp))
            if (timer.status == TimerEntity.STATUS_RUNNING && remainingMs > 0) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, "取消", tint = MaterialTheme.colorScheme.error)
                }
            } else {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "删除", tint = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
