package com.sfyc.countdownlist.compose.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sfyc.countdownlist.compose.theme.TimerFontFamily

enum class TimerDisplaySize {
    Large, Medium, Small, ListItem
}

@Composable
fun TimerDisplay(
    millis: Long,
    modifier: Modifier = Modifier,
    size: TimerDisplaySize = TimerDisplaySize.Medium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    showMillis: Boolean = false,
) {
    val text = formatTimerText(millis, showMillis)
    val style = when (size) {
        TimerDisplaySize.Large -> TextStyle(
            fontFamily = TimerFontFamily,
            fontSize = 56.sp,
            lineHeight = 64.sp,
            fontWeight = FontWeight.Bold,
        )
        TimerDisplaySize.Medium -> TextStyle(
            fontFamily = TimerFontFamily,
            fontSize = 44.sp,
            lineHeight = 52.sp,
            fontWeight = FontWeight.Bold,
        )
        TimerDisplaySize.Small -> TextStyle(
            fontFamily = TimerFontFamily,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.Medium,
        )
        TimerDisplaySize.ListItem -> TextStyle(
            fontFamily = TimerFontFamily,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier,
    )
}

fun formatTimerText(millis: Long, showMillis: Boolean = false): String {
    val totalSeconds = millis / 1000
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (showMillis) {
        val ms = (millis % 1000) / 10
        if (h > 0) "%02d:%02d:%02d.%02d".format(h, m, s, ms)
        else "%02d:%02d.%02d".format(m, s, ms)
    } else {
        if (h > 0) "%02d:%02d:%02d".format(h, m, s)
        else "%02d:%02d".format(m, s)
    }
}
