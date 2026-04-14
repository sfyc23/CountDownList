package com.sfyc.countdownlist.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.sfyc.countdownlist.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_DURATION_MS = "duration_ms"
        const val ACTION_START = "com.sfyc.countdownlist.ACTION_START_TIMER"
        const val ACTION_STOP = "com.sfyc.countdownlist.ACTION_STOP_TIMER"
    }

    data class TimerState(
        val remainingMs: Long = 0L,
        val totalMs: Long = 0L,
        val isRunning: Boolean = false,
    )

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var tickJob: Job? = null
    private var deadline = 0L
    private var totalMs = 0L

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    inner class LocalBinder : Binder() {
        val service: TimerForegroundService get() = this@TimerForegroundService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val durationMs = intent.getLongExtra(EXTRA_DURATION_MS, 60_000L)
                startTimer(durationMs)
            }
            ACTION_STOP -> stopTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer(durationMs: Long) {
        totalMs = durationMs
        deadline = SystemClock.elapsedRealtime() + durationMs
        _state.value = TimerState(remainingMs = durationMs, totalMs = durationMs, isRunning = true)

        startForeground(NOTIFICATION_ID, buildNotification(durationMs))

        tickJob?.cancel()
        tickJob = scope.launch {
            while (true) {
                val remaining = (deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0)
                _state.value = _state.value.copy(remainingMs = remaining)
                updateNotification(remaining)
                if (remaining <= 0) {
                    _state.value = _state.value.copy(isRunning = false)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    break
                }
                delay(1000)
            }
        }
    }

    fun stopTimer() {
        tickJob?.cancel()
        _state.value = TimerState()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "倒计时服务",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "后台倒计时进行中"
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(remainingMs: Long): Notification {
        val sec = (remainingMs / 1000).toInt()
        val text = String.format("剩余 %02d:%02d", sec / 60, sec % 60)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_arrow_back)
            .setContentTitle("倒计时进行中")
            .setContentText(text)
            .setOngoing(true)
            .setSilent(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotification(remainingMs: Long) {
        val notification = buildNotification(remainingMs)
        getSystemService(NotificationManager::class.java)?.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        tickJob?.cancel()
        scope.cancel()
    }
}
