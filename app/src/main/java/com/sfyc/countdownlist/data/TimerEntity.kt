package com.sfyc.countdownlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer_tasks")
data class TimerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val deadlineMs: Long,
    val durationMs: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = STATUS_PENDING,
) {
    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_RUNNING = "running"
        const val STATUS_FINISHED = "finished"
        const val STATUS_CANCELLED = "cancelled"
    }
}
