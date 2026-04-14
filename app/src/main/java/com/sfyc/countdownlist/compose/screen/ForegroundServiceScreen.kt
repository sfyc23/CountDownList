package com.sfyc.countdownlist.compose.screen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sfyc.countdownlist.compose.component.CircularTimerProgress
import com.sfyc.countdownlist.compose.component.StatusChip
import com.sfyc.countdownlist.compose.component.TimerDisplay
import com.sfyc.countdownlist.compose.component.TimerDisplaySize
import com.sfyc.countdownlist.compose.component.TimerStatus
import com.sfyc.countdownlist.compose.theme.LocalTimerColors
import com.sfyc.countdownlist.service.TimerForegroundService

@Composable
fun ForegroundServiceScreen() {
    val context = LocalContext.current
    val timerColors = LocalTimerColors.current

    val presets = listOf(30_000L, 60_000L, 120_000L, 300_000L)
    val presetLabels = listOf("30s", "1m", "2m", "5m")
    var selectedPreset by rememberSaveable { mutableIntStateOf(1) }

    var serviceBinder by remember { mutableStateOf<TimerForegroundService.LocalBinder?>(null) }
    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                serviceBinder = binder as? TimerForegroundService.LocalBinder
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                serviceBinder = null
            }
        }
    }

    DisposableEffect(Unit) {
        val intent = Intent(context, TimerForegroundService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        onDispose {
            try { context.unbindService(connection) } catch (_: Exception) {}
        }
    }

    val serviceState by serviceBinder?.service?.state?.collectAsState()
        ?: remember { mutableStateOf(TimerForegroundService.TimerState()) }

    val progress = if (serviceState.totalMs > 0) {
        serviceState.remainingMs.toFloat() / serviceState.totalMs
    } else 0f

    val progressColor = when {
        !serviceState.isRunning -> MaterialTheme.colorScheme.outlineVariant
        serviceState.remainingMs <= 10_000L -> timerColors.warning
        else -> timerColors.running
    }
    val status = when {
        !serviceState.isRunning && serviceState.totalMs > 0 && serviceState.remainingMs <= 0L -> TimerStatus.Finished
        serviceState.isRunning && serviceState.remainingMs <= 10_000L -> TimerStatus.Warning
        serviceState.isRunning -> TimerStatus.Running
        else -> TimerStatus.Idle
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Foreground Service 倒计时",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "退到后台仍持续运行，通知栏实时显示剩余时间。" +
                            "适合训练计时、专注钟等需要后台存活的场景。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            presets.forEachIndexed { i, _ ->
                FilterChip(
                    selected = selectedPreset == i,
                    onClick = { if (!serviceState.isRunning) selectedPreset = i },
                    label = { Text(presetLabels[i]) },
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        CircularTimerProgress(
            progress = progress,
            size = 220.dp,
            strokeWidth = 6.dp,
            progressColor = progressColor,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TimerDisplay(
                    millis = serviceState.remainingMs,
                    size = TimerDisplaySize.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                StatusChip(status = status)
            }
        }

        Spacer(Modifier.height(8.dp))

        if (serviceState.isRunning) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                ),
            ) {
                Text(
                    text = "通知栏已激活 — 退出此页面后倒计时将在通知栏继续",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        val shape = RoundedCornerShape(12.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!serviceState.isRunning) {
                Button(
                    onClick = {
                        val intent = Intent(context, TimerForegroundService::class.java).apply {
                            action = TimerForegroundService.ACTION_START
                            putExtra(TimerForegroundService.EXTRA_DURATION_MS, presets[selectedPreset])
                        }
                        context.startService(intent)
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text("开始")
                }
            } else {
                OutlinedButton(
                    onClick = {
                        serviceBinder?.service?.stopTimer()
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) {
                    Icon(Icons.Default.Stop, null)
                    Spacer(Modifier.width(8.dp))
                    Text("停止")
                }
            }
        }
    }
}
