package com.sfyc.countdownlist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sfyc.countdownlist.receiver.TimerExpiredReceiver;
import com.sfyc.countdownlist.utils.PrefUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author :leilei on 2017/3/27 1445.
 * 当切换屏幕，没有退出这个Activity时，也就是OnPause时，会调成Alarm闹铃
 */
public class AlarmActivity extends AppCompatActivity {

    private enum TimerState {
        STOPPED,
        RUNNING
    }

    private Context mContext;

    @BindView(R.id.tv_countTime)
    TextView mTimerTv;

    @BindView(R.id.btn_start)
    Button mStartBtn;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private static final long MAX_TIME = 12 * 1000;
    PrefUtils mPreferences;

    private static final long TIMER_LENGHT = 10; // Ten seconds
    private long mTimeToGo;
    private CountDownTimer mCountDownTimer;
    private TimerState mState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        ButterKnife.bind(this);
        mContext = this;
        toolbar.setTitle(R.string.title_alarm_user);
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
        if (mState == TimerState.RUNNING) {
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
            mTimeToGo = (TIMER_LENGHT - (getNow() - startTime));
            if (mTimeToGo <= 0) { // TIMER EXPIRED
                mTimeToGo = TIMER_LENGHT;
                mState = TimerState.STOPPED;
                onTimerFinish();
            } else {
                startTimer();
                mState = TimerState.RUNNING;
            }
        } else {
            mTimeToGo = TIMER_LENGHT;
            mState = TimerState.STOPPED;
        }
        updateTimeUi();
    }

    private void onTimerFinish() {
//        Toast.makeText(this, R.string.timer_finished, Toast.LENGTH_SHORT).show();
        mPreferences.setStartedTime(0);
        mTimeToGo = TIMER_LENGHT;
        updateTimeUi();
    }

    private void updateTimeUi() {
        if (mState == TimerState.RUNNING) {
            mStartBtn.setEnabled(false);
        } else {
            mStartBtn.setEnabled(true);
        }
        mTimerTv.setText(String.valueOf(mTimeToGo));
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeToGo * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                mTimeToGo -= 1;
                updateTimeUi();
            }
            public void onFinish() {
                mState = TimerState.STOPPED;
                onTimerFinish();
                updateTimeUi();
            }
        }.start();
    }



    public void setAlarm() {
        long wakeUpTime = (mPreferences.getStartedTime() + TIMER_LENGHT) * 1000;
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TimerExpiredReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(wakeUpTime, sender), sender);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, wakeUpTime, sender);
        }
    }

    public void removeAlarm() {
        Intent intent = new Intent(this, TimerExpiredReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }

    @OnClick({R.id.btn_start, R.id.btn_cancel, R.id.btn_pause, R.id.btn_resume})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if  (mState == TimerState.STOPPED) {
                    mPreferences.setStartedTime(getNow());
                    startTimer();
                    mState = TimerState.RUNNING;
                }
                break;
            case R.id.btn_cancel:

                break;
            case R.id.btn_pause:

                break;
            case R.id.btn_resume:

                break;
            default:
                break;
        }
    }
}
