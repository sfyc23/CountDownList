package com.sfyc.countdownlist.compose.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sfyc.countdownlist.compose.theme.TimerFontFamily
import kotlinx.coroutines.delay

enum class FlipStyle(val label: String, val bg: Color, val fg: Color) {
    Classic("经典黑", Color(0xFF1C1917), Color(0xFFF5F5F4)),
    White("白色", Color(0xFFF5F5F4), Color(0xFF1C1917)),
    Colorful("彩色", Color(0xFF312E81), Color(0xFFA5B4FC)),
}

@Composable
fun FlipClockScreen() {
    var totalSeconds by rememberSaveable { mutableLongStateOf(300L) }
    var remaining by rememberSaveable { mutableLongStateOf(300L) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var selectedStyle by remember { mutableIntStateOf(0) }
    val style = FlipStyle.entries[selectedStyle]

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (remaining > 0) {
                delay(1000L)
                remaining--
            }
            isRunning = false
        }
    }

    val minutes = (remaining / 60).toInt()
    val seconds = (remaining % 60).toInt()
    val d1 = minutes / 10
    val d2 = minutes % 10
    val d3 = seconds / 10
    val d4 = seconds % 10

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FlipDigit(digit = d1, style = style)
            Spacer(Modifier.width(4.dp))
            FlipDigit(digit = d2, style = style)

            Text(
                text = ":",
                fontFamily = TimerFontFamily,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = style.fg.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 6.dp),
            )

            FlipDigit(digit = d3, style = style)
            Spacer(Modifier.width(4.dp))
            FlipDigit(digit = d4, style = style)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FlipStyle.entries.forEachIndexed { index, s ->
                FilterChip(
                    selected = selectedStyle == index,
                    onClick = { selectedStyle = index },
                    label = { Text(s.label) },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("开始")
                }
            } else {
                Button(
                    onClick = { isRunning = false },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
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
                Icon(Icons.Default.Stop, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("重置")
            }
        }
    }
}

@Composable
private fun FlipDigit(
    digit: Int,
    style: FlipStyle,
) {
    var prevDigit by remember { mutableIntStateOf(digit) }
    val rotation by animateFloatAsState(
        targetValue = if (digit != prevDigit) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "flip",
        finishedListener = { prevDigit = digit },
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = 56.dp, height = 80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(style.bg)
            .graphicsLayer { rotationX = rotation.coerceIn(0f, 90f) * 0f },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(style.bg),
            ) {
                Text(
                    text = "$digit",
                    fontFamily = TimerFontFamily,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.fg,
                    modifier = Modifier.padding(bottom = 0.dp),
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = style.fg.copy(alpha = 0.15f),
            )
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(style.bg.copy(alpha = 0.9f)),
            ) {
                Text(
                    text = "$digit",
                    fontFamily = TimerFontFamily,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.fg.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 0.dp),
                )
            }
        }
    }
}
