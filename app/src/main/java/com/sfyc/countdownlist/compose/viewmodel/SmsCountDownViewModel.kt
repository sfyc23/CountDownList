package com.sfyc.countdownlist.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SmsState(
    val phone: String = "",
    val code: String = "",
    val remainingSeconds: Int = 0,
    val isCounting: Boolean = false,
    val isCodeSent: Boolean = false,
    val verifyResult: String? = null,
)

class SmsCountDownViewModel : ViewModel() {

    companion object {
        private const val COUNTDOWN_SECONDS = 60
    }

    private val _state = MutableStateFlow(SmsState())
    val state: StateFlow<SmsState> = _state.asStateFlow()

    private var countdownJob: Job? = null
    /** 当前生成的随机验证码，每次发送时刷新 */
    private var currentCode: String = ""

    fun onPhoneChanged(phone: String) {
        _state.value = _state.value.copy(phone = phone, verifyResult = null)
    }

    fun onCodeChanged(code: String) {
        _state.value = _state.value.copy(code = code, verifyResult = null)
    }

    fun sendCode() {
        val phone = _state.value.phone
        if (phone.isBlank()) {
            _state.value = _state.value.copy(verifyResult = "请输入手机号")
            return
        }
        if (_state.value.isCounting) return

        currentCode = (1000..9999).random().toString()
        _state.value = _state.value.copy(
            isCounting = true,
            isCodeSent = true,
            remainingSeconds = COUNTDOWN_SECONDS,
            verifyResult = "验证码已发送（模拟验证码: $currentCode）",
        )
        startCountdown()
    }

    fun verify() {
        val s = _state.value
        if (s.code.isBlank()) {
            _state.value = s.copy(verifyResult = "请输入验证码")
            return
        }
        if (s.code == currentCode) {
            _state.value = s.copy(verifyResult = "验证成功!")
        } else {
            _state.value = s.copy(verifyResult = "验证码错误")
        }
    }

    fun cancelCountdown() {
        countdownJob?.cancel()
        _state.value = _state.value.copy(
            isCounting = false,
            remainingSeconds = 0,
        )
    }

    fun reset() {
        countdownJob?.cancel()
        currentCode = ""
        _state.value = SmsState()
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var remaining = COUNTDOWN_SECONDS
            while (remaining > 0) {
                delay(1000L)
                remaining--
                _state.value = _state.value.copy(remainingSeconds = remaining)
            }
            _state.value = _state.value.copy(isCounting = false, remainingSeconds = 0)
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
