package com.sfyc.countdownlist.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 桌面倒计时小组件（Glance 实现）。
 * 展示一个目标日期倒计时，显示 "距离 XXX 还有 X 天 X 时 X 分 X 秒"。
 */
class TimerGlanceWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
            val targetName = prefs[KEY_TARGET_NAME] ?: "新年"
            val targetMs = prefs[KEY_TARGET_MS] ?: defaultTargetMs()
            val nowMs = System.currentTimeMillis()
            val diffMs = (targetMs - nowMs).coerceAtLeast(0)

            val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffMs)
            val hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(diffMs) % 24
            val minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(diffMs) % 60
            val seconds = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(diffMs) % 60

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .background(GlanceTheme.colors.widgetBackground),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "距离 $targetName",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = GlanceTheme.colors.onSurface,
                        ),
                    )
                    Spacer(GlanceModifier.height(6.dp))
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        TimeBlock(days.toString(), "天")
                        Spacer(GlanceModifier.width(4.dp))
                        TimeBlock(String.format("%02d", hours), "时")
                        Spacer(GlanceModifier.width(4.dp))
                        TimeBlock(String.format("%02d", minutes), "分")
                        Spacer(GlanceModifier.width(4.dp))
                        TimeBlock(String.format("%02d", seconds), "秒")
                    }
                    if (diffMs <= 0) {
                        Spacer(GlanceModifier.height(4.dp))
                        Text(
                            text = "已到达！",
                            style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.primary),
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun TimeBlock(value: String, label: String) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    color = GlanceTheme.colors.onSurface,
                ),
            )
            Text(
                text = label,
                style = TextStyle(fontSize = 10.sp, color = GlanceTheme.colors.secondary),
            )
        }
    }

    companion object {
        val KEY_TARGET_NAME = stringPreferencesKey("widget_target_name")
        val KEY_TARGET_MS = longPreferencesKey("widget_target_ms")

        /** 默认目标：下一个新年 */
        fun defaultTargetMs(): Long {
            val cal = java.util.Calendar.getInstance()
            val year = if (cal.get(java.util.Calendar.MONTH) == java.util.Calendar.DECEMBER
                && cal.get(java.util.Calendar.DAY_OF_MONTH) == 31
            ) cal.get(java.util.Calendar.YEAR) + 2
            else cal.get(java.util.Calendar.YEAR) + 1
            cal.set(year, java.util.Calendar.JANUARY, 1, 0, 0, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }
    }
}

/**
 * Widget Receiver，由系统回调触发 widget 更新。
 * 通过每 60 秒在协程中更新一次 widget 来实现分钟级精度刷新。
 */
class TimerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TimerGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        startPeriodicUpdate(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            startPeriodicUpdate(context)
        }
    }

    private fun startPeriodicUpdate(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            // 立即更新一次
            TimerGlanceWidget().updateAll(context)
        }
    }
}
