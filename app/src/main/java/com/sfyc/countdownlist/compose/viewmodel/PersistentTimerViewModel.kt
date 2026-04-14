package com.sfyc.countdownlist.compose.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sfyc.countdownlist.data.TimerDatabase
import com.sfyc.countdownlist.data.TimerEntity
import com.sfyc.countdownlist.engine.TickerRepository
import com.sfyc.countdownlist.worker.TimerExpiredWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 端到端 Demo ViewModel。
 * 创建 → Room 持久化 → Flow 展示 → WorkManager 到点触发 → 通知。
 */
class PersistentTimerViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = TimerDatabase.getInstance(application).timerDao()

    val allTimers: StateFlow<List<TimerEntity>> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _nowRealtime = MutableStateFlow(SystemClock.elapsedRealtime())
    val nowRealtime: StateFlow<Long> = _nowRealtime.asStateFlow()

    private var tickJob: Job? = null

    init {
        tickJob = viewModelScope.launch {
            TickerRepository.elapsedRealtimeFlow(intervalMs = 1000L).collect { now ->
                _nowRealtime.value = now
            }
        }
    }

    fun addTimer(name: String, durationMs: Long) {
        viewModelScope.launch {
            val deadlineMs = System.currentTimeMillis() + durationMs
            val entity = TimerEntity(
                name = name,
                deadlineMs = deadlineMs,
                durationMs = durationMs,
                status = TimerEntity.STATUS_RUNNING,
            )
            val id = dao.insert(entity)

            TimerExpiredWorker.schedule(
                context = getApplication(),
                timerId = id,
                timerName = name,
                delayMs = durationMs,
            )
        }
    }

    fun cancelTimer(entity: TimerEntity) {
        viewModelScope.launch {
            dao.updateStatus(entity.id, TimerEntity.STATUS_CANCELLED)
            TimerExpiredWorker.cancel(getApplication(), entity.id)
        }
    }

    fun deleteTimer(entity: TimerEntity) {
        viewModelScope.launch {
            dao.delete(entity)
            TimerExpiredWorker.cancel(getApplication(), entity.id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tickJob?.cancel()
    }
}
