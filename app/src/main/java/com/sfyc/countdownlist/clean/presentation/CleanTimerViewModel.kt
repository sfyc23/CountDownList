package com.sfyc.countdownlist.clean.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sfyc.countdownlist.clean.data.RoomTimerTaskRepository
import com.sfyc.countdownlist.clean.domain.AddTimerUseCase
import com.sfyc.countdownlist.clean.domain.CancelTimerUseCase
import com.sfyc.countdownlist.clean.domain.DeleteTimerUseCase
import com.sfyc.countdownlist.clean.domain.ObserveTimersUseCase
import com.sfyc.countdownlist.clean.domain.TimerTaskModel
import com.sfyc.countdownlist.data.TimerDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Presentation 层 ViewModel。
 * 只依赖 Domain 层 UseCase，不直接操作 Room。
 *
 * 在真实项目中 UseCase 应通过 DI 注入；
 * 此处为演示 Clean Architecture 分层，手动在 init 中构造。
 */
class CleanTimerViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = TimerDatabase.getInstance(app).timerDao()
    private val repo = RoomTimerTaskRepository(dao)

    private val observeTimers = ObserveTimersUseCase(repo)
    private val addTimer = AddTimerUseCase(repo)
    private val cancelTimer = CancelTimerUseCase(repo)
    private val deleteTimer = DeleteTimerUseCase(repo)

    val timers: StateFlow<List<TimerTaskModel>> = observeTimers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(name: String, durationMs: Long) {
        viewModelScope.launch {
            try {
                addTimer(name, durationMs)
            } catch (_: IllegalArgumentException) {
                // 校验失败，可扩展为 UI 事件
            }
        }
    }

    fun cancel(id: Long) {
        viewModelScope.launch { cancelTimer(id) }
    }

    fun delete(id: Long) {
        viewModelScope.launch { deleteTimer(id) }
    }
}
