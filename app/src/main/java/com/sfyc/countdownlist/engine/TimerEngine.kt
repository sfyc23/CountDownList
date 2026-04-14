package com.sfyc.countdownlist.engine

import kotlinx.coroutines.flow.StateFlow

/**
 * 计时引擎抽象接口。
 * 将"计时逻辑"与"显示层"彻底分离。
 * Widget、通知栏、Activity、Compose 页面都只订阅 remainingFlow。
 */
interface TimerEngine {
    val remainingFlow: StateFlow<Long>
    val isRunningFlow: StateFlow<Boolean>

    fun start(durationMs: Long)
    fun pause()
    fun resume()
    fun cancel()
}
