package com.sfyc.countdownlist.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RepeatTimerState(
    val durationSeconds: Int = 30,
    val remainingMs: Long = 30_000L,
    val currentRound: Int = 0,
    val totalRounds: Int = 5,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isInfinite: Boolean = false,
    val roundJustFinished: Boolean = false,
)

class RepeatTimerViewModel : ViewModel() {

    private val _state = MutableStateFlow(RepeatTimerState())
    val state: StateFlow<RepeatTimerState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun setConfig(durationSec: Int, rounds: Int, infinite: Boolean) {
        if (_state.value.isRunning) return
        _state.value = RepeatTimerState(
            durationSeconds = durationSec,
            remainingMs = durationSec * 1000L,
            totalRounds = rounds,
            isInfinite = infinite,
        )
    }

    fun start() {
        if (_state.value.isRunning) return
        _state.value = _state.value.copy(
            isRunning = true,
            isPaused = false,
            currentRound = 1,
            remainingMs = _state.value.durationSeconds * 1000L,
            roundJustFinished = false,
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

    fun stop() {
        timerJob?.cancel()
        _state.value = RepeatTimerState(
            durationSeconds = _state.value.durationSeconds,
            remainingMs = _state.value.durationSeconds * 1000L,
            totalRounds = _state.value.totalRounds,
            isInfinite = _state.value.isInfinite,
        )
    }

    private fun startTicking() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.remainingMs > 0) {
                delay(1000L)
                val newRemaining = (_state.value.remainingMs - 1000).coerceAtLeast(0)
                _state.value = _state.value.copy(remainingMs = newRemaining)
            }
            onRoundComplete()
        }
    }

    private fun onRoundComplete() {
        val s = _state.value
        _state.value = s.copy(roundJustFinished = true)

        val canContinue = s.isInfinite || s.currentRound < s.totalRounds
        if (canContinue) {
            _state.value = _state.value.copy(
                currentRound = s.currentRound + 1,
                remainingMs = s.durationSeconds * 1000L,
                roundJustFinished = true,
            )
            startTicking()
        } else {
            _state.value = _state.value.copy(isRunning = false)
        }
    }

    fun clearRoundFinished() {
        _state.value = _state.value.copy(roundJustFinished = false)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
