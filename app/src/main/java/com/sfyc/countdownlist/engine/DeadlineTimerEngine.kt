package com.sfyc.countdownlist.engine

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 基于 Deadline + elapsedRealtime 的 TimerEngine 实现。
 * 精度高、抗重建、不受系统时间修改影响。
 */
class DeadlineTimerEngine(
    private val scope: CoroutineScope,
) : TimerEngine {

    private val _remaining = MutableStateFlow(0L)
    override val remainingFlow: StateFlow<Long> = _remaining.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    override val isRunningFlow: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var deadline = 0L
    private var pausedRemaining = 0L
    private var tickJob: Job? = null

    override fun start(durationMs: Long) {
        deadline = SystemClock.elapsedRealtime() + durationMs
        pausedRemaining = durationMs
        _remaining.value = durationMs
        _isRunning.value = true
        startTicking()
    }

    override fun pause() {
        if (!_isRunning.value) return
        tickJob?.cancel()
        pausedRemaining = (deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0)
        _remaining.value = pausedRemaining
    }

    override fun resume() {
        if (pausedRemaining <= 0) return
        deadline = SystemClock.elapsedRealtime() + pausedRemaining
        startTicking()
    }

    override fun cancel() {
        tickJob?.cancel()
        _remaining.value = 0
        _isRunning.value = false
        deadline = 0
        pausedRemaining = 0
    }

    private fun startTicking() {
        tickJob?.cancel()
        tickJob = scope.launch {
            TickerRepository.elapsedRealtimeFlow(intervalMs = 200L).collect { now ->
                val remaining = (deadline - now).coerceAtLeast(0)
                _remaining.value = remaining
                if (remaining <= 0L) {
                    _isRunning.value = false
                    tickJob?.cancel()
                }
            }
        }
    }
}
