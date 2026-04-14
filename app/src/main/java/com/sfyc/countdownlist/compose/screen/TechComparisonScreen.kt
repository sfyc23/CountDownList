package com.sfyc.countdownlist.compose.screen

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class TechRow(
    val name: String,
    val precision: String,
    val background: String,
    val lifecycle: String,
    val configChange: String,
    val minApi: String,
    val recommendation: String,
)

private val techRows = listOf(
    TechRow("Timer", "秒", "✗", "✗", "✗", "1", "简单定时"),
    TechRow("Handler", "秒", "✗", "✗", "✗", "1", "简单定时"),
    TechRow("CountDownTimer", "秒", "✗", "✗", "✗", "1", "通用倒计时"),
    TechRow("ValueAnimator", "帧", "✗", "✗", "✗", "11", "动画关联"),
    TechRow("Chronometer", "秒", "✗", "✓", "✗", "1", "正计时 View"),
    TechRow("AlarmManager", "分", "✓", "✓", "✓", "1", "精确闹钟"),
    TechRow("RxJava", "秒", "✗", "△", "✗", "-", "响应式链"),
    TechRow("Coroutine", "秒", "✗", "✓", "△", "-", "现代协程"),
    TechRow("Flow", "秒", "✗", "✓", "✓", "-", "推荐方案"),
    TechRow("Compose", "秒", "✗", "✓", "✓", "-", "声明式 UI"),
    TechRow("FG Service", "秒", "✓", "✓", "✓", "-", "后台常驻"),
    TechRow("WorkManager", "分", "✓", "✓", "✓", "-", "可靠后台"),
    TechRow("Choreographer", "帧", "✗", "✗", "✗", "16", "帧级动画"),
)

private val headers = listOf("方案", "精度", "后台", "生命周期", "配置变更", "API", "推荐场景")

private data class ScenarioCard(
    val title: String,
    val recommendation: String,
    val reason: String,
)

private val scenarios = listOf(
    ScenarioCard(
        title = "简单页面倒计时",
        recommendation = "Coroutine + Flow",
        reason = "生命周期安全、代码简洁、官方推荐",
    ),
    ScenarioCard(
        title = "后台长时间计时",
        recommendation = "Foreground Service + Flow",
        reason = "后台存活、通知栏展示、系统不杀",
    ),
    ScenarioCard(
        title = "精确闹钟提醒",
        recommendation = "AlarmManager + BroadcastReceiver",
        reason = "系统级唤醒、即使 App 被杀也能触发",
    ),
    ScenarioCard(
        title = "列表批量倒计时",
        recommendation = "统一 Ticker + Deadline 模型",
        reason = "避免每个 item 独立计时器，性能更优",
    ),
    ScenarioCard(
        title = "动画/视觉倒计时",
        recommendation = "ValueAnimator / Compose Animatable",
        reason = "帧级平滑渲染、与 UI 动画深度集成",
    ),
    ScenarioCard(
        title = "短信验证码",
        recommendation = "CountDownTimer / Coroutine",
        reason = "固定时长、UI 强绑定、无需后台",
    ),
)

@Composable
fun TechComparisonScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            text = "技术方案对比矩阵",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "✓ 支持  ✗ 不支持  △ 部分支持",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))

        ComparisonTable()

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        Text(
            text = "场景推荐",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(12.dp))

        scenarios.forEach { scenario ->
            ScenarioRecommendCard(scenario)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ComparisonTable() {
    val colWidths = listOf(100.dp, 48.dp, 48.dp, 64.dp, 64.dp, 40.dp, 80.dp)

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
    ) {
        Column {
            Row {
                headers.forEachIndexed { i, h ->
                    Text(
                        text = h,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(colWidths[i])
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                    )
                }
            }
            HorizontalDivider()
            techRows.forEachIndexed { idx, row ->
                val values = listOf(
                    row.name, row.precision, row.background,
                    row.lifecycle, row.configChange, row.minApi, row.recommendation,
                )
                Row {
                    values.forEachIndexed { i, v ->
                        Text(
                            text = v,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = if (i == 0) TextAlign.Start else TextAlign.Center,
                            fontWeight = if (i == 0) FontWeight.Medium else FontWeight.Normal,
                            color = when (v) {
                                "✓" -> MaterialTheme.colorScheme.primary
                                "✗" -> MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                                "△" -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.onSurface
                            },
                            modifier = Modifier
                                .width(colWidths[i])
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                        )
                    }
                }
                if (idx < techRows.lastIndex) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ScenarioRecommendCard(scenario: ScenarioCard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = scenario.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "推荐: ${scenario.recommendation}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "原因: ${scenario.reason}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
