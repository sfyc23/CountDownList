package com.sfyc.countdownlist.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class DateTargetItem(
    val id: Int,
    val title: String,
    val targetTimestamp: Long,
    val remainingMillis: Long,
)

data class DateTargetState(
    val targets: List<DateTargetItem> = emptyList(),
)

class DateTargetViewModel : ViewModel() {

    private val _state = MutableStateFlow(DateTargetState())
    val state: StateFlow<DateTargetState> = _state.asStateFlow()

    private var tickJob: Job? = null

    init {
        loadPresets()
        startTick()
    }

    private fun loadPresets() {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        cal.set(2027, Calendar.JUNE, 7, 9, 0, 0)
        val gaokao = cal.timeInMillis

        cal.set(2027, Calendar.JANUARY, 1, 0, 0, 0)
        val newYear = cal.timeInMillis

        cal.timeInMillis = now
        cal.add(Calendar.DAY_OF_YEAR, 100)
        val day100 = cal.timeInMillis

        val presets = listOf(
            DateTargetItem(1, "2027 高考", gaokao, gaokao - now),
            DateTargetItem(2, "2027 元旦", newYear, newYear - now),
            DateTargetItem(3, "100天后", day100, day100 - now),
        )
        _state.value = DateTargetState(targets = presets)
    }

    private fun startTick() {
        tickJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val now = System.currentTimeMillis()
                _state.value = _state.value.copy(
                    targets = _state.value.targets.map { item ->
                        item.copy(remainingMillis = (item.targetTimestamp - now).coerceAtLeast(0))
                    }
                )
            }
        }
    }

    fun addTarget(title: String, year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance()
        cal.set(year, month, day, 0, 0, 0)
        val timestamp = cal.timeInMillis
        val now = System.currentTimeMillis()
        val nextId = (_state.value.targets.maxOfOrNull { it.id } ?: 0) + 1
        val item = DateTargetItem(nextId, title, timestamp, (timestamp - now).coerceAtLeast(0))
        _state.value = _state.value.copy(targets = listOf(item) + _state.value.targets)
    }

    override fun onCleared() {
        super.onCleared()
        tickJob?.cancel()
    }
}
