package com.sfyc.countdownlist.clean.domain

import kotlinx.coroutines.flow.Flow

/**
 * Domain 层 UseCase 集合。
 * 封装业务规则，Presentation 层只通过 UseCase 与数据交互。
 */
class ObserveTimersUseCase(private val repo: TimerTaskRepository) {
    operator fun invoke(): Flow<List<TimerTaskModel>> = repo.observeAll()
}

class AddTimerUseCase(private val repo: TimerTaskRepository) {
    suspend operator fun invoke(name: String, durationMs: Long): TimerTaskModel {
        require(name.isNotBlank()) { "任务名不能为空" }
        require(durationMs > 0) { "时长必须大于 0" }
        return repo.add(name, durationMs)
    }
}

class CancelTimerUseCase(private val repo: TimerTaskRepository) {
    suspend operator fun invoke(id: Long) = repo.cancel(id)
}

class DeleteTimerUseCase(private val repo: TimerTaskRepository) {
    suspend operator fun invoke(id: Long) = repo.delete(id)
}
