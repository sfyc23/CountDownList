package com.sfyc.countdownlist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sfyc.countdownlist.receiver.TimerExpiredReceiver;
import com.sfyc.countdownlist.utils.PrefUtils;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {

    private enum TimerState {
        STOPPED,
        RUNNING
    }

    private TextView mTimerTv;
    private Button mStartBtn;
    private Toolbar toolbar;

    private static final long TIMER_LENGTH = 10;

    private PrefUtils mPreferences;
    private long mTimeToGo;
    private CountDownTimer mCountDownTimer;
    private TimerState mState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        mTimerTv = findViewById(R.id.tv_countTime);
        mStartBtn = findViewById(R.id.btn_start);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_alarm_user);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        mPreferences = new PrefUtils(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTimer();
        removeAlarm();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mState == TimerState.RUNNING && mCountDownTimer != null) {
            mCountDownTimer.cancel();
            setAlarm();
        }
    }

    private long getNow() {
        Calendar rightNow = Calendar.getInstance();
        return rightNow.getTimeInMillis() / 1000;
    }

    private void initTimer() {
        long startTime = mPreferences.getStartedTime();
        if (startTime > 0) {
            mTimeToGo = TIMER_LENGTH - (getNow() - startTime);
            if (mTimeToGo <= 0) {
                mTimeToGo = TIMER_LENGTH;
                mState = TimerState.STOPPED;
                onTimerFinish();
            } else {
                startTimer();
                mState = TimerState.RUNNING;
            }
        } else {
            mTimeToGo = TIMER_LENGTH;
            mState = TimerState.STOPPED;
        }
        updateTimeUi();
    }

    private void onTimerFinish() {
        mPreferences.setStartedTime(0);
        mTimeToGo = TIMER_LENGTH;
        updateTimeUi();
    }

    private void updateTimeUi() {
        mStartBtn.setEnabled(mState != TimerState.RUNNING);
        mTimerTv.setText(String.valueOf(mTimeToGo));
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeToGo * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeToGo -= 1;
                updateTimeUi();
            }

            @Override
            public void onFinish() {
                mState = TimerState.STOPPED;
                onTimerFinish();
                updateTimeUi();
            }
        }.start();
    }

    private int pendingIntentFlags(int baseFlags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return baseFlags | PendingIntent.FLAG_IMMUTABLE;
        }
        return baseFlags;
    }

    public void setAlarm() {
        long wakeUpTime = (mPreferences.getStartedTime() + TIMER_LENGTH) * 1000;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        Intent intent = new Intent(this, TimerExpiredReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                pendingIntentFlags(PendingIntent.FLAG_CANCEL_CURRENT)
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(wakeUpTime, sender), sender);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, wakeUpTime, sender);
        }
    }

    public void removeAlarm() {
        Intent intent = new Intent(this, TimerExpiredReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                pendingIntentFlags(PendingIntent.FLAG_CANCEL_CURRENT)
        );
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(sender);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_start && mState == TimerState.STOPPED) {
            mPreferences.setStartedTime(getNow());
            startTimer();
            mState = TimerState.RUNNING;
            updateTimeUi();
        } else if (viewId == R.id.btn_cancel && mState == TimerState.RUNNING) {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
            mState = TimerState.STOPPED;
            onTimerFinish();
        }
    }
}
