package com.sfyc.countdownlist.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * 倒计时音效 / 震动反馈工具。
 *
 * 使用方式：
 *   val feedback = FeedbackUtil(context)
 *   feedback.tick()           // 最后几秒的 tick
 *   feedback.finish()         // 结束时的完成音 + 长震动
 *   feedback.release()        // Activity 销毁时释放资源
 */
class FeedbackUtil(context: Context) {

    private val appContext: Context = context.applicationContext
    private val vibrator: Vibrator? = obtainVibrator(appContext)
    private val soundPool: SoundPool
    private var tickSoundId: Int = 0
    private var finishSoundId: Int = 0
    private var loaded = false

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder().setMaxStreams(2).setAudioAttributes(attrs).build()

        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) loaded = true
        }

        // Android 内置 tone 资源在各 OEM 上不一定存在，
        // 这里用简短的 SoundPool 占位，实际项目可替换为自定义 raw 资源
        try {
            val tickUri = "android.resource://${appContext.packageName}/raw/tick"
            val finishUri = "android.resource://${appContext.packageName}/raw/finish"
            // 尝试加载自定义 raw 资源；不存在时 soundPool 返回 0，不会崩溃
            @Suppress("DEPRECATION")
            tickSoundId = soundPool.load(appContext, resolveRawId(appContext, "tick"), 1)
            @Suppress("DEPRECATION")
            finishSoundId = soundPool.load(appContext, resolveRawId(appContext, "finish"), 1)
        } catch (_: Exception) {
            // raw 资源不存在时安静降级，仅保留震动
        }
    }

    /** 短促 tick 震动（最后 3 秒每秒调用一次） */
    fun tick() {
        vibrateMs(50)
        if (loaded && tickSoundId != 0) {
            soundPool.play(tickSoundId, 0.5f, 0.5f, 1, 0, 1f)
        }
    }

    /** 完成时的长震动 + 音效 */
    fun finish() {
        vibrateMs(300)
        if (loaded && finishSoundId != 0) {
            soundPool.play(finishSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    /** 简单短震动 */
    fun vibrate(ms: Long = 200) {
        vibrateMs(ms)
    }

    fun release() {
        soundPool.release()
    }

    @Suppress("DEPRECATION")
    private fun vibrateMs(ms: Long) {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                it.vibrate(ms)
            }
        }
    }

    companion object {
        @Suppress("DEPRECATION")
        private fun obtainVibrator(context: Context): Vibrator? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val mgr = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                mgr?.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        }

        private fun resolveRawId(context: Context, name: String): Int {
            return context.resources.getIdentifier(name, "raw", context.packageName)
        }
    }
}
