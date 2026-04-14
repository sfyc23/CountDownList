package com.sfyc.countdownlist.compose.screen

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class MoreEntry(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val key: String,
)

private val entries = listOf(
    MoreEntry(Icons.Default.Sms, "短信验证码", "Compose 实现的短信倒计时表单", "sms"),
    MoreEntry(Icons.Default.Timelapse, "番茄钟", "工作/休息交替的多阶段计时", "pomodoro"),
    MoreEntry(Icons.Default.Animation, "翻页时钟", "经典翻页数字效果倒计时", "flip"),
    MoreEntry(Icons.Default.Code, "声明式倒计时", "LaunchedEffect + rememberSaveable 驱动", "declarative"),
    MoreEntry(Icons.Default.Schedule, "Deadline 模型", "基于截止时间的抗重建倒计时", "deadline"),
    MoreEntry(Icons.Default.NotificationsActive, "后台 Service", "Foreground Service + 通知栏实时更新", "service"),
    MoreEntry(Icons.Default.Storage, "持久化 Demo", "Room + Flow + WorkManager 端到端", "persistent"),
    MoreEntry(Icons.Default.Repeat, "循环倒计时", "自动重复 + 震动反馈的多轮倒计时", "repeat"),
    MoreEntry(Icons.Default.CloudSync, "服务端校准", "模拟 NTP 对时，对比校准前后差异", "serversync"),
    MoreEntry(Icons.Default.Widgets, "桌面小组件", "Glance App Widget 目标日期倒计时", "widget_info"),
    MoreEntry(Icons.Default.Notifications, "通知栏倒计时", "常驻通知 + 自定义 RemoteViews 布局", "notification"),
    MoreEntry(Icons.Default.Architecture, "Clean Arch", "Domain → Data → Presentation 三层分离", "clean"),
    MoreEntry(Icons.AutoMirrored.Filled.CompareArrows, "技术对比", "各方案精度/性能/适用场景对比", "tech"),
)

@Composable
fun MoreScreen(
    onNavigate: (String) -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Text(
                text = "更多功能",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            )
        }

        items(entries) { entry ->
            MoreEntryCard(
                entry = entry,
                onClick = { onNavigate(entry.key) },
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun MoreEntryCard(
    entry: MoreEntry,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = entry.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = entry.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
