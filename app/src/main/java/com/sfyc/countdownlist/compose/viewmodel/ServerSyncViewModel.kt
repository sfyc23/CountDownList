package com.sfyc.countdownlist.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ServerSyncState(
    val serverTimeMs: Long = 0L,
    val localTimeMs: Long = 0L,
    val offsetMs: Long = 0L,
    val isSynced: Boolean = false,
    val deadlineMs: Long = 0L,
    val remainingRaw: Long = 0L,
    val remainingSynced: Long = 0L,
    val isRunning: Boolean = false,
)

/**
 * 模拟服务端时间校准：
 * 1) "请求"服务端时间 → fakeServerTime (带 0~3 秒随机偏移模拟时钟不同步)
 * 2) 计算偏移 offset = serverTime - localTime
 * 3) 启动倒计时时同时展示「无校准」和「有校准」结果差异
 */
class ServerSyncViewModel : ViewModel() {

    private val _state = MutableStateFlow(ServerSyncState())
    val state: StateFlow<ServerSyncState> = _state.asStateFlow()

    private var timerJob: Job? = null

    /** 模拟随机偏移量（-3000 ~ +3000 ms）*/
    private var fakeOffset: Long = 0L

    fun syncTime() {
        fakeOffset = (-3000L..3000L).random()
        val localNow = System.currentTimeMillis()
        val fakeServerNow = localNow + fakeOffset
        _state.value = _state.value.copy(
            serverTimeMs = fakeServerNow,
            localTimeMs = localNow,
            offsetMs = fakeOffset,
            isSynced = true,
        )
    }

    fun start(durationSec: Int = 30) {
        timerJob?.cancel()
        val localNow = System.currentTimeMillis()
        val deadline = localNow + fakeOffset + durationSec * 1000L
        _state.value = _state.value.copy(
            deadlineMs = deadline,
            isRunning = true,
        )
        timerJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val rawRemaining = (deadline - now).coerceAtLeast(0)
                val syncedRemaining = (deadline - (now + fakeOffset)).coerceAtLeast(0)
                _state.value = _state.value.copy(
                    remainingRaw = rawRemaining,
                    remainingSynced = syncedRemaining,
                )
                if (syncedRemaining <= 0L) {
                    _state.value = _state.value.copy(isRunning = false)
                    break
                }
                delay(200L)
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
        _state.value = _state.value.copy(
            isRunning = false,
            remainingRaw = 0L,
            remainingSynced = 0L,
        )
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
