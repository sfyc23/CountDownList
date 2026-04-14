package com.sfyc.countdownlist.engine

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 全局统一时钟中心。
 * 所有需要每秒 tick 的页面/组件都订阅同一个 Flow，
 * 避免每个页面各自启动独立计时器。
 *
 * 用法:
 *   XML Activity  → lifecycleScope.launch { repeatOnLifecycle(STARTED) { TickerRepository.tickerFlow().collect { ... } } }
 *   Compose       → val tick by TickerRepository.tickerFlow().collectAsStateWithLifecycle(0L)
 */
object TickerRepository {

    /**
     * 产生每秒递增的 tick 流（冷流）。
     * 每个 collector 都有独立的计数器，但共用同一个调度逻辑。
     * 返回值为自订阅以来经过的秒数（0, 1, 2, ...）
     */
    fun tickerFlow(intervalMs: Long = 1000L): Flow<Long> = flow {
        var tick = 0L
        while (true) {
            emit(tick++)
            delay(intervalMs)
        }
    }

    /**
     * 返回当前 elapsedRealtime 的 tick 流，
     * 适合 Deadline 模型用 deadline - currentTick 计算剩余时间。
     */
    fun elapsedRealtimeFlow(intervalMs: Long = 1000L): Flow<Long> = flow {
        while (true) {
            emit(android.os.SystemClock.elapsedRealtime())
            delay(intervalMs)
        }
    }
}
