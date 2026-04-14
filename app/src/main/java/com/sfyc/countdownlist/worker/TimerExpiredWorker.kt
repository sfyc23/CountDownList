package com.sfyc.countdownlist.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sfyc.countdownlist.R
import com.sfyc.countdownlist.data.TimerDatabase
import com.sfyc.countdownlist.data.TimerEntity
import java.util.concurrent.TimeUnit

/**
 * WorkManager 延迟任务。
 * 倒计时结束后触发通知，即使 App 被杀也能执行。
 * 与 AlarmManager 互补：WorkManager 保证执行但不精确到秒。
 */
class TimerExpiredWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    companion object {
        const val KEY_TIMER_ID = "timer_id"
        const val KEY_TIMER_NAME = "timer_name"
        private const val CHANNEL_ID = "timer_expired_channel"
        private const val NOTIFICATION_ID_BASE = 2000

        fun schedule(context: Context, timerId: Long, timerName: String, delayMs: Long) {
            val request = OneTimeWorkRequestBuilder<TimerExpiredWorker>()
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        KEY_TIMER_ID to timerId,
                        KEY_TIMER_NAME to timerName,
                    )
                )
                .addTag("timer_$timerId")
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }

        fun cancel(context: Context, timerId: Long) {
            WorkManager.getInstance(context).cancelAllWorkByTag("timer_$timerId")
        }
    }

    override suspend fun doWork(): Result {
        val timerId = inputData.getLong(KEY_TIMER_ID, -1)
        val timerName = inputData.getString(KEY_TIMER_NAME) ?: "倒计时"

        if (timerId > 0) {
            val dao = TimerDatabase.getInstance(applicationContext).timerDao()
            dao.updateStatus(timerId, TimerEntity.STATUS_FINISHED)
        }

        showNotification(timerId, timerName)
        return Result.success()
    }

    private fun showNotification(timerId: Long, timerName: String) {
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "倒计时完成",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply { description = "倒计时任务到期通知" }
            nm.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_arrow_back)
            .setContentTitle("倒计时完成")
            .setContentText("「$timerName」已到期")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        nm.notify(NOTIFICATION_ID_BASE + timerId.toInt(), notification)
    }
}
