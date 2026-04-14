package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sfyc.countdownlist.utils.TimeTools;

public class CountDownSimpleActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private CountDownTimer mCountDownTimer;

    private static final long MAX_TIME = 62 * 1000;
    private long curTime = 0;
    private boolean isPause = false;

    private TextView mTimerTv;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        mContext = this;
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_countdown_user);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        mTimerTv = findViewById(R.id.tv_countTime);

        initCountDownTimer(MAX_TIME);
        mCountDownTimer.start();
    }

    public void initCountDownTimer(long millisInFuture) {
        mCountDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                curTime = millisUntilFinished;
                mTimerTv.setText(TimeTools.getCountTimeByLong(millisUntilFinished));
                isPause = false;
            }

            @Override
            public void onFinish() {
                mTimerTv.setText("Done!");
            }
        };
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_start) {
            isPause = false;
            mCountDownTimer.start();
        } else if (viewId == R.id.btn_cancel) {
            isPause = false;
            mCountDownTimer.cancel();
        } else if (viewId == R.id.btn_pause) {
            if (!isPause) {
                isPause = true;
                mCountDownTimer.cancel();
            }
        } else if (viewId == R.id.btn_resume && curTime != 0 && isPause) {
            initCountDownTimer(curTime);
            mCountDownTimer.start();
            isPause = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }
}
