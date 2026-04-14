package com.sfyc.countdownlist.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val TimerFontFamily = FontFamily.Monospace

// ── Semantic timer colors ──────────────────────────────────────────────
data class TimerColors(
    val running: Color,
    val paused: Color,
    val warning: Color,
    val finished: Color,
    val stopwatch: Color,
)

val LocalTimerColors = staticCompositionLocalOf {
    TimerColors(
        running = Color(0xFF4F46E5),
        paused = Color(0xFFF59E0B),
        warning = Color(0xFFEF4444),
        finished = Color(0xFF57534E),
        stopwatch = Color(0xFF10B981),
    )
}

private val LightTimerColors = TimerColors(
    running = Color(0xFF4F46E5),
    paused = Color(0xFFF59E0B),
    warning = Color(0xFFEF4444),
    finished = Color(0xFF57534E),
    stopwatch = Color(0xFF10B981),
)

private val DarkTimerColors = TimerColors(
    running = Color(0xFFA5B4FC),
    paused = Color(0xFFFCD34D),
    warning = Color(0xFFFCA5A5),
    finished = Color(0xFFA8A29E),
    stopwatch = Color(0xFF6EE7B7),
)

// ── Light Color Scheme ─────────────────────────────────────────────────
private val LightColors = lightColorScheme(
    primary = Color(0xFF4F46E5),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = Color(0xFFEC4899),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFCE7F3),
    onSecondaryContainer = Color(0xFF831843),
    tertiary = Color(0xFF10B981),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF064E3B),
    error = Color(0xFFEF4444),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    surface = Color(0xFFFAFAFA),
    onSurface = Color(0xFF1C1917),
    surfaceVariant = Color(0xFFF5F5F4),
    onSurfaceVariant = Color(0xFF57534E),
    outline = Color(0xFFD6D3D1),
    outlineVariant = Color(0xFFE7E5E4),
)

// ── Dark Color Scheme ──────────────────────────────────────────────────
private val DarkColors = darkColorScheme(
    primary = Color(0xFFA5B4FC),
    onPrimary = Color(0xFF312E81),
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFFF9A8D4),
    onSecondary = Color(0xFF831843),
    secondaryContainer = Color(0xFF9D174D),
    onSecondaryContainer = Color(0xFFFCE7F3),
    tertiary = Color(0xFF6EE7B7),
    onTertiary = Color(0xFF064E3B),
    tertiaryContainer = Color(0xFF065F46),
    onTertiaryContainer = Color(0xFFD1FAE5),
    error = Color(0xFFFCA5A5),
    onError = Color(0xFF7F1D1D),
    errorContainer = Color(0xFF991B1B),
    onErrorContainer = Color(0xFFFEE2E2),
    surface = Color(0xFF1C1917),
    onSurface = Color(0xFFF5F5F4),
    surfaceVariant = Color(0xFF292524),
    onSurfaceVariant = Color(0xFFA8A29E),
    outline = Color(0xFF44403C),
    outlineVariant = Color(0xFF292524),
)

// ── Typography ─────────────────────────────────────────────────────────
private val CountDownTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = TimerFontFamily,
        fontSize = 56.sp,
        lineHeight = 64.sp,
        fontWeight = FontWeight.Bold,
    ),
    displayMedium = TextStyle(
        fontFamily = TimerFontFamily,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        fontWeight = FontWeight.Bold,
    ),
    displaySmall = TextStyle(
        fontFamily = TimerFontFamily,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        fontWeight = FontWeight.Medium,
    ),
)

// ── Theme ──────────────────────────────────────────────────────────────
@Composable
fun CountDownTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val timerColors = if (darkTheme) DarkTimerColors else LightTimerColors

    CompositionLocalProvider(LocalTimerColors provides timerColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CountDownTypography,
            content = content,
        )
    }
}
