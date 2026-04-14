package com.sfyc.countdownlist;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChronometerActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Chronometer mChronometer;

    private static final long MAX_TIME = 12 * 1000;

    // 暂停时记录剩余毫秒数
    private long mTimeLeftOnPause = 0;
    private boolean isPaused = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chronometer);
        toolbar = findViewById(R.id.toolbar);
        mChronometer = findViewById(R.id.chronometer);
        toolbar.setTitle(R.string.title_chronometer_user);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);

        mChronometer.setBase(SystemClock.elapsedRealtime() + MAX_TIME);
        mChronometer.setCountDown(true);
        mChronometer.start();
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long remaining = chronometer.getBase() - SystemClock.elapsedRealtime();
                if (remaining <= 0) {
                    chronometer.stop();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_start) {
            isPaused = false;
            mChronometer.setBase(SystemClock.elapsedRealtime() + MAX_TIME);
            mChronometer.start();
        } else if (viewId == R.id.btn_cancel) {
            isPaused = false;
            mChronometer.stop();
        } else if (viewId == R.id.btn_pause) {
            if (!isPaused) {
                // 记录剩余时间并暂停
                mTimeLeftOnPause = mChronometer.getBase() - SystemClock.elapsedRealtime();
                mChronometer.stop();
                isPaused = true;
            }
        } else if (viewId == R.id.btn_resume) {
            if (isPaused) {
                // 用记录的剩余时间恢复
                mChronometer.setBase(SystemClock.elapsedRealtime() + mTimeLeftOnPause);
                mChronometer.start();
                isPaused = false;
            }
        }
    }
}
