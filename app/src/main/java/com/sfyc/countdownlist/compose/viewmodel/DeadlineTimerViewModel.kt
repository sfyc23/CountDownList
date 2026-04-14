package com.sfyc.countdownlist.compose.viewmodel

import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfyc.countdownlist.engine.TickerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Deadline-based 倒计时 ViewModel。
 * 只持久化 deadline（目标时刻的 elapsedRealtime），
 * 界面每次用 deadline - now 反推剩余时间，天然抗重建。
 */
data class DeadlineUiState(
    val remainingMs: Long = 0L,
    val totalMs: Long = 60_000L,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
)

class DeadlineTimerViewModel(
    private val savedState: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(DeadlineUiState())
    val state: StateFlow<DeadlineUiState> = _state.asStateFlow()

    private var tickJob: Job? = null

    private var deadline: Long
        get() = savedState["deadline"] ?: 0L
        set(value) { savedState["deadline"] = value }

    private var totalMs: Long
        get() = savedState["totalMs"] ?: 60_000L
        set(value) { savedState["totalMs"] = value }

    private var pausedRemaining: Long
        get() = savedState["pausedRemaining"] ?: 60_000L
        set(value) { savedState["pausedRemaining"] = value }

    init {
        if (deadline > 0 && deadline > SystemClock.elapsedRealtime()) {
            _state.value = DeadlineUiState(
                remainingMs = deadline - SystemClock.elapsedRealtime(),
                totalMs = totalMs,
                isRunning = true,
            )
            startTicking()
        } else {
            _state.value = DeadlineUiState(totalMs = totalMs)
        }
    }

    fun setDuration(ms: Long) {
        if (_state.value.isRunning) return
        totalMs = ms
        pausedRemaining = ms
        _state.value = DeadlineUiState(totalMs = ms)
    }

    fun start() {
        if (_state.value.isRunning) return
        deadline = SystemClock.elapsedRealtime() + pausedRemaining
        totalMs = pausedRemaining
        _state.value = _state.value.copy(isRunning = true, isPaused = false, totalMs = totalMs)
        startTicking()
    }

    fun pause() {
        if (!_state.value.isRunning || _state.value.isPaused) return
        tickJob?.cancel()
        pausedRemaining = (deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0)
        _state.value = _state.value.copy(isPaused = true)
    }

    fun resume() {
        if (!_state.value.isPaused) return
        deadline = SystemClock.elapsedRealtime() + pausedRemaining
        _state.value = _state.value.copy(isPaused = false)
        startTicking()
    }

    fun cancel() {
        tickJob?.cancel()
        deadline = 0L
        pausedRemaining = totalMs
        _state.value = DeadlineUiState(totalMs = totalMs)
    }

    private fun startTicking() {
        tickJob?.cancel()
        tickJob = viewModelScope.launch {
            TickerRepository.elapsedRealtimeFlow(intervalMs = 100L).collect { now ->
                val remaining = (deadline - now).coerceAtLeast(0)
                _state.value = _state.value.copy(remainingMs = remaining)
                if (remaining <= 0L) {
                    _state.value = _state.value.copy(isRunning = false)
                    tickJob?.cancel()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tickJob?.cancel()
    }
}
