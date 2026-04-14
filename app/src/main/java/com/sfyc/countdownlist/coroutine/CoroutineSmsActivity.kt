package com.sfyc.countdownlist.coroutine

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.core.content.ContextCompat
import com.sfyc.countdownlist.R
import com.sfyc.countdownlist.databinding.ActivityCoroutineSmsBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 协程 + XML ViewBinding 实现短信验证码倒计时。
 */
class CoroutineSmsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoroutineSmsBinding

    companion object {
        private const val COUNTDOWN_SECONDS = 60
    }

    private var countdownJob: Job? = null
    private var currentCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoroutineSmsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViewById<Toolbar>(R.id.toolbar).title = "协程 短信倒计时"

        binding.btnSend.setOnClickListener { sendCode() }
        binding.btnVerify.setOnClickListener { verify() }
        binding.btnReset.setOnClickListener { reset() }
    }

    private fun sendCode() {
        val phone = binding.etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show()
            return
        }
        if (countdownJob?.isActive == true) return

        currentCode = (1000..9999).random().toString()
        binding.tvResult.text = "验证码已发送（模拟验证码：$currentCode）"
        binding.tvResult.setTextColor(ContextCompat.getColor(this, R.color.md_primary))

        startCountdown()
    }

    private fun verify() {
        val input = binding.etCode.text.toString().trim()
        when {
            input.isEmpty() -> {
                binding.tvResult.text = "请输入验证码"
                binding.tvResult.setTextColor(ContextCompat.getColor(this, R.color.md_error))
            }
            currentCode.isEmpty() -> {
                binding.tvResult.text = "请先发送验证码"
                binding.tvResult.setTextColor(ContextCompat.getColor(this, R.color.md_error))
            }
            input == currentCode -> {
                binding.tvResult.text = "验证成功"
                binding.tvResult.setTextColor(ContextCompat.getColor(this, R.color.md_tertiary))
            }
            else -> {
                binding.tvResult.text = "验证码错误"
                binding.tvResult.setTextColor(ContextCompat.getColor(this, R.color.md_error))
            }
        }
    }

    private fun reset() {
        countdownJob?.cancel()
        currentCode = ""
        binding.etPhone.text?.clear()
        binding.etCode.text?.clear()
        binding.tvResult.text = ""
        binding.btnSend.isEnabled = true
        binding.btnSend.text = "发送验证码"
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = lifecycleScope.launch {
            binding.btnSend.isEnabled = false
            var remaining = COUNTDOWN_SECONDS
            while (remaining > 0) {
                binding.btnSend.text = "${remaining}s"
                delay(1000L)
                remaining--
            }
            binding.btnSend.isEnabled = true
            binding.btnSend.text = "发送验证码"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownJob?.cancel()
    }
}
