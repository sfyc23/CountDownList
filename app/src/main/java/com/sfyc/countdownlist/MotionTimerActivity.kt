package com.sfyc.countdownlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import android.widget.TextView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * MotionLayout 动画倒计时演示。
 * 三种状态之间通过 MotionLayout transition 切换：
 *   idle   → 数字居中，进度环隐藏
 *   running → 数字移到顶部，进度环展开
 *   finished → 全屏闪烁覆盖
 */
class MotionTimerActivity : AppCompatActivity() {

    private lateinit var motionLayout: MotionLayout
    private lateinit var tvTime: TextView
    private lateinit var tvStatus: TextView
    private lateinit var progressCircular: CircularProgressIndicator
    private lateinit var btnAction: MaterialButton

    private var timerJob: Job? = null
    private var totalSeconds = 10
    private var remainingSeconds = 10

    private enum class State { IDLE, RUNNING, FINISHED }
    private var currentState = State.IDLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motion_timer)

        motionLayout = findViewById(R.id.motion_layout)
        tvTime = findViewById(R.id.tv_time)
        tvStatus = findViewById(R.id.tv_status)
        progressCircular = findViewById(R.id.progress_circular)
        btnAction = findViewById(R.id.btn_action)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        updateTimeDisplay()

        btnAction.setOnClickListener {
            when (currentState) {
                State.IDLE -> startTimer()
                State.RUNNING -> cancelTimer()
                State.FINISHED -> resetTimer()
            }
        }
    }

    private fun startTimer() {
        currentState = State.RUNNING
        btnAction.text = "取消"
        tvStatus.text = "运行中"
        remainingSeconds = totalSeconds
        progressCircular.max = totalSeconds * 100

        motionLayout.transitionToState(R.id.state_running)

        timerJob = lifecycleScope.launch {
            while (remainingSeconds > 0) {
                updateTimeDisplay()
                val progressValue = (remainingSeconds.toFloat() / totalSeconds * totalSeconds * 100).toInt()
                progressCircular.setProgressCompat(progressValue, true)
                delay(1000L)
                remainingSeconds--
            }
            updateTimeDisplay()
            progressCircular.setProgressCompat(0, true)
            onFinished()
        }
    }

    private fun cancelTimer() {
        timerJob?.cancel()
        resetTimer()
    }

    private fun resetTimer() {
        timerJob?.cancel()
        currentState = State.IDLE
        remainingSeconds = totalSeconds
        btnAction.text = "开始"
        tvStatus.text = ""
        updateTimeDisplay()
        motionLayout.transitionToState(R.id.state_idle)
    }

    private fun onFinished() {
        currentState = State.FINISHED
        btnAction.text = "重置"
        tvStatus.text = "已完成！"

        motionLayout.transitionToState(R.id.state_finished)
    }

    private fun updateTimeDisplay() {
        val min = remainingSeconds / 60
        val sec = remainingSeconds % 60
        tvTime.text = String.format("%02d:%02d", min, sec)
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
    }
}
