package com.sfyc.countdownlist.coroutine

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.sfyc.countdownlist.R
import com.sfyc.countdownlist.databinding.ActivityCoroutineSingleBinding
import com.sfyc.countdownlist.utils.TimeTools
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 协程 + XML ViewBinding 实现单个倒计时。
 * 使用 lifecycleScope 自动跟随 Activity 生命周期取消任务。
 */
class CoroutineSingleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoroutineSingleBinding

    private val totalMillis = 60_000L
    private var remainingMillis = totalMillis
    private var timerJob: Job? = null
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoroutineSingleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViewById<Toolbar>(R.id.toolbar).title = "协程 单个倒计时"
        updateUi()

        binding.btnStart.setOnClickListener {
            remainingMillis = totalMillis
            isPaused = false
            startTimer()
        }
        binding.btnPause.setOnClickListener {
            if (timerJob?.isActive == true && !isPaused) {
                isPaused = true
                timerJob?.cancel()
                binding.tvStatus.text = "已暂停"
            }
        }
        binding.btnResume.setOnClickListener {
            if (isPaused && remainingMillis > 0) {
                isPaused = false
                startTimer()
            }
        }
        binding.btnCancel.setOnClickListener {
            timerJob?.cancel()
            isPaused = false
            remainingMillis = totalMillis
            binding.tvStatus.text = ""
            updateUi()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        binding.tvStatus.text = ""
        timerJob = lifecycleScope.launch {
            while (remainingMillis > 0) {
                updateUi()
                delay(1000L)
                remainingMillis = (remainingMillis - 1000L).coerceAtLeast(0)
            }
            updateUi()
            binding.tvStatus.text = "时间到"
        }
    }

    private fun updateUi() {
        binding.tvTimer.text = TimeTools.getCountTimeByLong(remainingMillis)
        val progress = if (totalMillis > 0) {
            (remainingMillis * 1000 / totalMillis).toInt()
        } else {
            0
        }
        binding.progressBar.progress = progress
    }
}
