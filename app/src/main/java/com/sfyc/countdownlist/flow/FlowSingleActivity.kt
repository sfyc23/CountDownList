package com.sfyc.countdownlist.flow

import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sfyc.countdownlist.R
import com.sfyc.countdownlist.engine.TickerRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.LinearProgressIndicator
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Flow + XML 单个倒计时 Demo。
 * 使用 TickerRepository.elapsedRealtimeFlow() 作为统一时钟源，
 * 配合 repeatOnLifecycle 实现生命周期安全的 UI 刷新。
 */
class FlowSingleActivity : AppCompatActivity() {

    private val totalSeconds = 60L
    private var deadlineRealtime = 0L
    private var pausedRemaining = totalSeconds * 1000L
    private var isRunning = false
    private var isPaused = false
    private var tickJob: Job? = null

    private lateinit var tvTime: TextView
    private lateinit var tvStatus: TextView
    private lateinit var progress: LinearProgressIndicator
    private lateinit var btnStart: MaterialButton
    private lateinit var btnPause: MaterialButton
    private lateinit var btnCancel: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow_single)

        findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = "Flow 单个倒计时"
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { finish() }
        }

        tvTime = findViewById(R.id.tv_time)
        tvStatus = findViewById(R.id.tv_status)
        progress = findViewById(R.id.progress)
        btnStart = findViewById(R.id.btn_start)
        btnPause = findViewById(R.id.btn_pause)
        btnCancel = findViewById(R.id.btn_cancel)

        progress.max = (totalSeconds * 1000).toInt()
        progress.progress = progress.max

        btnStart.setOnClickListener { start() }
        btnPause.setOnClickListener { togglePause() }
        btnCancel.setOnClickListener { cancel() }
    }

    private fun start() {
        deadlineRealtime = SystemClock.elapsedRealtime() + pausedRemaining
        isRunning = true
        isPaused = false
        startCollecting()
    }

    private fun togglePause() {
        if (!isRunning) return
        if (isPaused) {
            deadlineRealtime = SystemClock.elapsedRealtime() + pausedRemaining
            isPaused = false
            startCollecting()
        } else {
            isPaused = true
            pausedRemaining = deadlineRealtime - SystemClock.elapsedRealtime()
            tickJob?.cancel()
            updateStatusText("已暂停", R.color.timer_paused)
        }
    }

    private fun cancel() {
        tickJob?.cancel()
        isRunning = false
        isPaused = false
        pausedRemaining = totalSeconds * 1000L
        updateUI(totalSeconds * 1000L)
        updateStatusText("就绪", R.color.md_on_surface_variant)
    }

    private fun startCollecting() {
        tickJob?.cancel()
        tickJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                TickerRepository.elapsedRealtimeFlow(intervalMs = 100L).collect { now ->
                    val remaining = (deadlineRealtime - now).coerceAtLeast(0)
                    updateUI(remaining)
                    if (remaining <= 0L) {
                        isRunning = false
                        updateStatusText("已完成", R.color.timer_finished)
                        tickJob?.cancel()
                    } else if (remaining <= 10_000L) {
                        updateStatusText("即将结束", R.color.timer_warning)
                    } else {
                        updateStatusText("运行中", R.color.timer_running)
                    }
                }
            }
        }
    }

    private fun updateUI(remainingMs: Long) {
        val totalSec = (remainingMs / 1000).toInt()
        val min = totalSec / 60
        val sec = totalSec % 60
        tvTime.text = String.format("%02d:%02d", min, sec)
        progress.progress = remainingMs.toInt()
    }

    private fun updateStatusText(text: String, colorRes: Int) {
        tvStatus.text = text
        tvStatus.setTextColor(ContextCompat.getColor(this, colorRes))
    }
}
