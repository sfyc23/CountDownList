package com.sfyc.countdownlist.compose.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class SingleTimerState(
    val totalMillis: Long = 60_000L,
    val remainingMillis: Long = 60_000L,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
)

class SingleCountDownViewModel : ViewModel() {

    private val _state = MutableStateFlow(SingleTimerState())
    val state: StateFlow<SingleTimerState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun setDuration(seconds: Long) {
        if (_state.value.isRunning) return
        val millis = seconds * 1000
        _state.value = SingleTimerState(totalMillis = millis, remainingMillis = millis)
    }

    fun start() {
        timerJob?.cancel()
        val total = _state.value.totalMillis
        _state.value = SingleTimerState(
            totalMillis = total,
            remainingMillis = total,
            isRunning = true,
        )
        startTicking()
    }

    fun pause() {
        if (!_state.value.isRunning || _state.value.isPaused) return
        timerJob?.cancel()
        _state.value = _state.value.copy(isPaused = true)
    }

    fun resume() {
        if (!_state.value.isPaused) return
        _state.value = _state.value.copy(isPaused = false)
        startTicking()
    }

    fun cancel() {
        timerJob?.cancel()
        val total = _state.value.totalMillis
        _state.value = SingleTimerState(totalMillis = total, remainingMillis = total)
    }

    private fun startTicking() {
        timerJob = viewModelScope.launch {
            while (_state.value.remainingMillis > 0) {
                delay(1000L)
                val newRemaining = (_state.value.remainingMillis - 1000).coerceAtLeast(0)
                _state.value = _state.value.copy(remainingMillis = newRemaining)
                if (newRemaining <= 0) {
                    _state.value = _state.value.copy(
                        isRunning = false,
                        isFinished = true,
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
