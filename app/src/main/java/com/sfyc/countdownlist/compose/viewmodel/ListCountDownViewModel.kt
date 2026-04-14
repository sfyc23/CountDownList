package com.sfyc.countdownlist.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TimerItemState(
    val id: Int,
    val name: String,
    /** 到期时间戳（毫秒） */
    val expirationTime: Long,
    /** 初始总时长（毫秒），用于计算进度 */
    val totalDuration: Long,
    /** 当前剩余毫秒 */
    val remainingMillis: Long,
)

class ListCountDownViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<TimerItemState>>(emptyList())
    val items: StateFlow<List<TimerItemState>> = _items.asStateFlow()

    private var tickJob: Job? = null

    init {
        loadItems()
        startGlobalTick()
    }

    private fun loadItems() {
        val now = System.currentTimeMillis()
        val durations = listOf(15_000L, 30_000L, 45_000L, 60_000L, 90_000L, 120_000L, 180_000L, 300_000L)
        val names = listOf("任务 A", "任务 B", "任务 C", "任务 D", "任务 E", "任务 F", "任务 G", "任务 H")
        val list = durations.mapIndexed { index, duration ->
            TimerItemState(
                id = index + 1,
                name = names[index],
                expirationTime = now + duration,
                totalDuration = duration,
                remainingMillis = duration,
            )
        }
        _items.value = list
    }

    /** 单一协程统一刷新所有条目的剩余时间 */
    private fun startGlobalTick() {
        tickJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val now = System.currentTimeMillis()
                _items.value = _items.value.map { item ->
                    val remaining = (item.expirationTime - now).coerceAtLeast(0)
                    item.copy(remainingMillis = remaining)
                }
            }
        }
    }

    fun addItem() {
        val now = System.currentTimeMillis()
        val nextId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        val duration = (10_000L..120_000L).random()
        val newItem = TimerItemState(
            id = nextId,
            name = "新任务 $nextId",
            expirationTime = now + duration,
            totalDuration = duration,
            remainingMillis = duration,
        )
        _items.value = listOf(newItem) + _items.value
    }

    override fun onCleared() {
        super.onCleared()
        tickJob?.cancel()
    }
}
