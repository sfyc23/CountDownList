package com.sfyc.countdownlist.compose.screen

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun NotificationTimerScreen() {
    val context = LocalContext.current
    val timerColors = LocalTimerColors.current
    val shape = RoundedCornerShape(12.dp)

    var service by remember { mutableStateOf<TimerForegroundService?>(null) }
    val fallbackState = remember { MutableStateFlow(TimerForegroundService.TimerState()) }
    val timerState by (service?.state ?: fallbackState).collectAsState()

    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                service = (binder as? TimerForegroundService.LocalBinder)?.service
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                service = null
            }
        }
    }

    DisposableEffect(Unit) {
        val bindIntent = Intent(context, TimerForegroundService::class.java)
        context.bindService(bindIntent, connection, Context.BIND_AUTO_CREATE)
        onDispose {
            try { context.unbindService(connection) } catch (_: Exception) {}
        }
    }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    fun startService(durationMs: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        val intent = Intent(context, TimerForegroundService::class.java).apply {
            action = TimerForegroundService.ACTION_START
            putExtra(TimerForegroundService.EXTRA_DURATION_MS, durationMs)
        }
        context.startService(intent)
    }

    fun stopService() {
        service?.stopTimer()
    }

    val progress = if (timerState.totalMs > 0) timerState.remainingMs.toFloat() / timerState.totalMs else 0f
    val progressColor = when {
        !timerState.isRunning -> MaterialTheme.colorScheme.outlineVariant
        timerState.remainingMs <= 5_000 -> timerColors.warning
        else -> timerColors.running
    }
    val status = when {
        !timerState.isRunning && timerState.totalMs == 0L -> TimerStatus.Idle
        !timerState.isRunning -> TimerStatus.Finished
        timerState.remainingMs <= 5_000 -> TimerStatus.Warning
        else -> TimerStatus.Running
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
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "通知栏常驻倒计时",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "使用 Foreground Service 在通知栏实时显示倒计时。" +
                            "通知使用 setOngoing(true) 常驻，每秒通过 NotificationManager.notify() 更新内容。" +
                            "退出页面后倒计时仍在后台继续运行。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        CircularTimerProgress(
            progress = progress,
            size = 200.dp,
            strokeWidth = 6.dp,
            progressColor = progressColor,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TimerDisplay(
                    millis = timerState.remainingMs,
                    size = TimerDisplaySize.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                StatusChip(status = status)
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("技术要点", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "• NotificationCompat.Builder + setOngoing(true)\n" +
                            "• 可扩展为自定义 RemoteViews 通知布局\n" +
                            "• 与 ForegroundServiceScreen 共用同一个 Service\n" +
                            "• 通知栏更新频率：每秒一次（IMPORTANCE_LOW 静默）",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!timerState.isRunning) {
                Button(
                    onClick = { startService(120_000L) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = shape,
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text("2 分钟倒计时")
                }
            } else {
                OutlinedButton(
                    onClick = { stopService() },
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
