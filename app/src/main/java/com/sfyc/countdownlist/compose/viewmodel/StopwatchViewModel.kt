package com.sfyc.countdownlist.compose.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LapRecord(
    val index: Int,
    val splitMillis: Long,
    val totalMillis: Long,
)

data class StopwatchState(
    val elapsedMillis: Long = 0L,
    val isRunning: Boolean = false,
    val laps: List<LapRecord> = emptyList(),
)

class StopwatchViewModel : ViewModel() {

    private val _state = MutableStateFlow(StopwatchState())
    val state: StateFlow<StopwatchState> = _state.asStateFlow()

    private var tickJob: Job? = null
    private var startBase: Long = 0L
    private var accumulated: Long = 0L

    fun startOrResume() {
        if (_state.value.isRunning) return
        startBase = SystemClock.elapsedRealtime()
        _state.value = _state.value.copy(isRunning = true)
        tickJob = viewModelScope.launch {
            while (true) {
                delay(33L)
                val now = SystemClock.elapsedRealtime()
                val elapsed = accumulated + (now - startBase)
                _state.value = _state.value.copy(elapsedMillis = elapsed)
            }
        }
    }

    fun pause() {
        if (!_state.value.isRunning) return
        tickJob?.cancel()
        accumulated = _state.value.elapsedMillis
        _state.value = _state.value.copy(isRunning = false)
    }

    fun lap() {
        if (!_state.value.isRunning) return
        val current = _state.value
        val prevTotal = current.laps.lastOrNull()?.totalMillis ?: 0L
        val newLap = LapRecord(
            index = current.laps.size + 1,
            splitMillis = current.elapsedMillis - prevTotal,
            totalMillis = current.elapsedMillis,
        )
        _state.value = current.copy(laps = current.laps + newLap)
    }

    fun reset() {
        tickJob?.cancel()
        accumulated = 0L
        _state.value = StopwatchState()
    }

    override fun onCleared() {
        super.onCleared()
        tickJob?.cancel()
    }
}
