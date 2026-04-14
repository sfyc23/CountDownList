package com.sfyc.countdownlist.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PomodoroPhase { Work, Rest }

data class PomodoroState(
    val phase: PomodoroPhase = PomodoroPhase.Work,
    val currentRound: Int = 1,
    val totalRounds: Int = 4,
    val workMinutes: Int = 25,
    val restMinutes: Int = 5,
    val remainingMillis: Long = 25 * 60 * 1000L,
    val totalPhaseMillis: Long = 25 * 60 * 1000L,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val completedPomodoros: Int = 0,
    val totalFocusMinutes: Int = 0,
)

class PomodoroViewModel : ViewModel() {

    private val _state = MutableStateFlow(PomodoroState())
    val state: StateFlow<PomodoroState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun start() {
        if (_state.value.isRunning) return
        val s = _state.value
        val phaseMillis = when (s.phase) {
            PomodoroPhase.Work -> s.workMinutes * 60 * 1000L
            PomodoroPhase.Rest -> s.restMinutes * 60 * 1000L
        }
        _state.value = s.copy(
            isRunning = true,
            isPaused = false,
            remainingMillis = phaseMillis,
            totalPhaseMillis = phaseMillis,
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
        _state.value = PomodoroState(
            completedPomodoros = _state.value.completedPomodoros,
            totalFocusMinutes = _state.value.totalFocusMinutes,
        )
    }

    private fun startTicking() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.remainingMillis > 0) {
                delay(1000L)
                val newRemaining = (_state.value.remainingMillis - 1000).coerceAtLeast(0)
                _state.value = _state.value.copy(remainingMillis = newRemaining)
            }
            onPhaseComplete()
        }
    }

    private fun onPhaseComplete() {
        val s = _state.value
        when (s.phase) {
            PomodoroPhase.Work -> {
                val newCompleted = s.completedPomodoros + 1
                val newFocus = s.totalFocusMinutes + s.workMinutes
                if (s.currentRound >= s.totalRounds) {
                    _state.value = PomodoroState(
                        completedPomodoros = newCompleted,
                        totalFocusMinutes = newFocus,
                    )
                    return
                }
                val restMillis = s.restMinutes * 60 * 1000L
                _state.value = s.copy(
                    phase = PomodoroPhase.Rest,
                    remainingMillis = restMillis,
                    totalPhaseMillis = restMillis,
                    completedPomodoros = newCompleted,
                    totalFocusMinutes = newFocus,
                )
                startTicking()
            }
            PomodoroPhase.Rest -> {
                val workMillis = s.workMinutes * 60 * 1000L
                _state.value = s.copy(
                    phase = PomodoroPhase.Work,
                    currentRound = s.currentRound + 1,
                    remainingMillis = workMillis,
                    totalPhaseMillis = workMillis,
                )
                startTicking()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
