package com.sfyc.countdownlist.clean.presentation

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
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Cancel
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sfyc.countdownlist.clean.domain.TimerTaskModel

@Composable
fun CleanArchScreen(
    viewModel: CleanTimerViewModel = viewModel(),
) {
    val timers by viewModel.timers.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val names = listOf("专注", "阅读", "运动", "冥想", "休息")
                    val durations = listOf(25, 15, 10, 5, 3)
                    val idx = (0..4).random()
                    viewModel.add(names[idx], durations[idx] * 60_000L)
                },
            ) {
                Icon(Icons.Default.Add, "添加任务")
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                    ),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Architecture, null, tint = MaterialTheme.colorScheme.tertiary)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Clean Architecture 示例",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Domain 层: TimerTaskModel + UseCase (纯 Kotlin)\n" +
                                    "Data 层: RoomTimerTaskRepository → TimerDao\n" +
                                    "Presentation 层: CleanTimerViewModel → Compose UI\n\n" +
                                    "ViewModel 只依赖 UseCase，不直接操作 Room，实现关注点分离。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            if (timers.isEmpty()) {
                item {
                    Text(
                        text = "暂无任务，点击右下角 + 添加",
                        modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            items(timers, key = { it.id }) { task ->
                CleanTaskCard(
                    task = task,
                    onCancel = { viewModel.cancel(task.id) },
                    onDelete = { viewModel.delete(task.id) },
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun CleanTaskCard(
    task: TimerTaskModel,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
) {
    val remaining = task.remainingMs
    val sec = (remaining / 1000).toInt()
    val timeText = String.format("%02d:%02d", sec / 60, sec % 60)
    val statusText = when (task.status) {
        TimerTaskModel.Status.RUNNING -> if (remaining > 0) "运行中" else "已到期"
        TimerTaskModel.Status.FINISHED -> "已完成"
        TimerTaskModel.Status.CANCELLED -> "已取消"
        TimerTaskModel.Status.PENDING -> "等待中"
    }
    val statusColor = when (task.status) {
        TimerTaskModel.Status.RUNNING -> MaterialTheme.colorScheme.primary
        TimerTaskModel.Status.FINISHED -> MaterialTheme.colorScheme.tertiary
        TimerTaskModel.Status.CANCELLED -> MaterialTheme.colorScheme.error
        TimerTaskModel.Status.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(task.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(2.dp))
                Text(statusText, style = MaterialTheme.typography.bodySmall, color = statusColor)
            }
            Text(
                text = timeText,
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (task.status == TimerTaskModel.Status.RUNNING && remaining > 0) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Cancel, "取消", tint = MaterialTheme.colorScheme.error)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "删除", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
