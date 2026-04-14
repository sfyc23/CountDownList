package com.sfyc.countdownlist.compose.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sfyc.countdownlist.compose.theme.LocalTimerColors
import com.sfyc.countdownlist.compose.viewmodel.SmsCountDownViewModel

@Composable
fun SmsCountDownScreen(
    viewModel: SmsCountDownViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val timerColors = LocalTimerColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = state.phone,
                    onValueChange = { viewModel.onPhoneChanged(it) },
                    label = { Text("手机号") },
                    placeholder = { Text("请输入手机号") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = state.code,
                        onValueChange = { viewModel.onCodeChanged(it) },
                        label = { Text("验证码") },
                        placeholder = { Text("请输入验证码") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    FilledTonalButton(
                        onClick = { viewModel.sendCode() },
                        enabled = !state.isCounting && state.phone.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        if (!state.isCounting) {
                            Icon(
                                Icons.Outlined.Send,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 4.dp),
                            )
                        }
                        Text(
                            text = if (state.isCounting) "${state.remainingSeconds}s" else "发送",
                            maxLines = 1,
                        )
                    }
                }

                if (state.isCounting) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { state.remainingSeconds / 60f },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = timerColors.running,
                        trackColor = MaterialTheme.colorScheme.outlineVariant,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = { viewModel.verify() },
                        enabled = state.isCodeSent && state.code.isNotBlank(),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("验证")
                    }

                    OutlinedButton(
                        onClick = { viewModel.reset() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("重置")
                    }
                }
            }
        }

        state.verifyResult?.let { result ->
            Spacer(modifier = Modifier.height(16.dp))

            val isSuccess = result.contains("成功")
            val isError = result.contains("错误") || result.contains("请输入")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isSuccess -> MaterialTheme.colorScheme.primaryContainer
                        isError -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                ),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Outlined.CheckCircle else Icons.Outlined.Error,
                        contentDescription = null,
                        tint = when {
                            isSuccess -> MaterialTheme.colorScheme.onPrimaryContainer
                            isError -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                        },
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = result,
                        fontWeight = FontWeight.Medium,
                        color = when {
                            isSuccess -> MaterialTheme.colorScheme.onPrimaryContainer
                            isError -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                        },
                    )
                }
            }
        }

        if (state.isCounting) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "验证码将在 ${state.remainingSeconds} 秒后过期",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
