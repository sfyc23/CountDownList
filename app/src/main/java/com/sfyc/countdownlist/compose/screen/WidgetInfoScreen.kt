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
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WidgetInfoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Default.Widgets,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "桌面倒计时小组件",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "基于 Jetpack Glance 实现的 App Widget",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(24.dp))

        InfoCard(
            title = "如何添加",
            body = "长按桌面空白处 → 选择「小组件」→ 找到「倒计时」→ 拖动到桌面。",
        )
        Spacer(Modifier.height(12.dp))
        InfoCard(
            title = "功能说明",
            body = "默认显示距离下一个新年的倒计时，精确到秒。系统每 30 分钟自动刷新一次；Widget 打开时基于当前系统时间实时计算。",
        )
        Spacer(Modifier.height(12.dp))
        InfoCard(
            title = "技术要点",
            body = "• GlanceAppWidget + PreferencesGlanceStateDefinition\n• RemoteViews 替代方案，使用 Compose 风格 API\n• 通过 DataStore Preferences 存储目标日期\n• updatePeriodMillis 控制刷新频率（最小 30 分钟）",
        )
        Spacer(Modifier.height(12.dp))
        InfoCard(
            title = "与页面内倒计时的区别",
            body = "Widget 运行在 Launcher 进程中，不能使用协程/Flow 每秒刷新。需要通过 AlarmManager 或 WorkManager 做精确刷新，或依赖系统 updatePeriodMillis 做低频更新。",
        )
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
