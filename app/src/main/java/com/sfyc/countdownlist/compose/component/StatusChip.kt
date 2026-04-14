package com.sfyc.countdownlist.compose.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sfyc.countdownlist.compose.theme.LocalTimerColors

enum class TimerStatus { Idle, Running, Paused, Warning, Finished }

@Composable
fun StatusChip(
    status: TimerStatus,
    modifier: Modifier = Modifier,
) {
    val timerColors = LocalTimerColors.current

    val (bg, fg, label) = when (status) {
        TimerStatus.Idle -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "就绪",
        )
        TimerStatus.Running -> Triple(
            timerColors.running.copy(alpha = 0.12f),
            timerColors.running,
            "运行中",
        )
        TimerStatus.Paused -> Triple(
            timerColors.paused.copy(alpha = 0.12f),
            timerColors.paused,
            "已暂停",
        )
        TimerStatus.Warning -> Triple(
            timerColors.warning.copy(alpha = 0.12f),
            timerColors.warning,
            "即将结束",
        )
        TimerStatus.Finished -> Triple(
            timerColors.finished.copy(alpha = 0.12f),
            timerColors.finished,
            "已完成",
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bg,
    ) {
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
