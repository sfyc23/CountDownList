package com.sfyc.countdownlist.clean.data

import com.sfyc.countdownlist.clean.domain.TimerTaskModel
import com.sfyc.countdownlist.clean.domain.TimerTaskRepository
import com.sfyc.countdownlist.data.TimerDao
import com.sfyc.countdownlist.data.TimerEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Data 层：Room 实现的 TimerTaskRepository。
 * 负责将 Domain 层 Model ↔ Room Entity 互相映射。
 */
class RoomTimerTaskRepository(
    private val dao: TimerDao,
) : TimerTaskRepository {

    override fun observeAll(): Flow<List<TimerTaskModel>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun add(name: String, durationMs: Long): TimerTaskModel {
        val now = System.currentTimeMillis()
        val entity = TimerEntity(
            name = name,
            durationMs = durationMs,
            deadlineMs = now + durationMs,
            createdAt = now,
            status = TimerEntity.STATUS_RUNNING,
        )
        val id = dao.insert(entity)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun cancel(id: Long) {
        dao.updateStatus(id, TimerEntity.STATUS_CANCELLED)
    }

    override suspend fun delete(id: Long) {
        dao.getById(id)?.let { dao.delete(it) }
    }

    override suspend fun markFinished(id: Long) {
        dao.updateStatus(id, TimerEntity.STATUS_FINISHED)
    }
}

private fun TimerEntity.toDomain(): TimerTaskModel = TimerTaskModel(
    id = id,
    name = name,
    durationMs = durationMs,
    deadlineMs = deadlineMs,
    createdAt = createdAt,
    status = when (status) {
        TimerEntity.STATUS_RUNNING -> TimerTaskModel.Status.RUNNING
        TimerEntity.STATUS_FINISHED -> TimerTaskModel.Status.FINISHED
        TimerEntity.STATUS_CANCELLED -> TimerTaskModel.Status.CANCELLED
        else -> TimerTaskModel.Status.PENDING
    },
)
