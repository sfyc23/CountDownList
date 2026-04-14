package com.sfyc.countdownlist.clean.domain

/**
 * Domain 层数据模型（纯 Kotlin，不依赖任何 Android 或框架类）。
 */
data class TimerTaskModel(
    val id: Long = 0,
    val name: String,
    val durationMs: Long,
    val deadlineMs: Long,
    val status: Status,
    val createdAt: Long,
) {
    enum class Status { PENDING, RUNNING, FINISHED, CANCELLED }

    val remainingMs: Long
        get() = (deadlineMs - System.currentTimeMillis()).coerceAtLeast(0)
}
