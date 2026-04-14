package com.sfyc.countdownlist.clean.domain

import kotlinx.coroutines.flow.Flow

/**
 * Domain 层 Repository 接口（不依赖 Room 等具体实现）。
 */
interface TimerTaskRepository {
    fun observeAll(): Flow<List<TimerTaskModel>>
    suspend fun add(name: String, durationMs: Long): TimerTaskModel
    suspend fun cancel(id: Long)
    suspend fun delete(id: Long)
    suspend fun markFinished(id: Long)
}
