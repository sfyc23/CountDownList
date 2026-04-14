package com.sfyc.countdownlist.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore 替代 SharedPreferences。
 * 支持 Flow 响应式读取、协程异步写入、类型安全。
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timer_prefs")

object TimerPreferencesKeys {
    val DEFAULT_DURATION = longPreferencesKey("default_duration_ms")
    val POMODORO_WORK_MINUTES = intPreferencesKey("pomodoro_work_min")
    val POMODORO_REST_MINUTES = intPreferencesKey("pomodoro_rest_min")
    val POMODORO_ROUNDS = intPreferencesKey("pomodoro_rounds")
}

class TimerPreferences(private val context: Context) {

    val defaultDurationFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[TimerPreferencesKeys.DEFAULT_DURATION] ?: 60_000L
    }

    val pomodoroWorkMinutesFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[TimerPreferencesKeys.POMODORO_WORK_MINUTES] ?: 25
    }

    val pomodoroRestMinutesFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[TimerPreferencesKeys.POMODORO_REST_MINUTES] ?: 5
    }

    val pomodoroRoundsFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[TimerPreferencesKeys.POMODORO_ROUNDS] ?: 4
    }

    suspend fun setDefaultDuration(ms: Long) {
        context.dataStore.edit { prefs ->
            prefs[TimerPreferencesKeys.DEFAULT_DURATION] = ms
        }
    }

    suspend fun setPomodoroConfig(workMin: Int, restMin: Int, rounds: Int) {
        context.dataStore.edit { prefs ->
            prefs[TimerPreferencesKeys.POMODORO_WORK_MINUTES] = workMin
            prefs[TimerPreferencesKeys.POMODORO_REST_MINUTES] = restMin
            prefs[TimerPreferencesKeys.POMODORO_ROUNDS] = rounds
        }
    }
}
