package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sfyc.countdownlist.utils.CountDown;
import com.sfyc.countdownlist.utils.TimeTools;

/**
 * Author :leilei on 2016/11/11 2300.
 */
public class CountDownCustomActivity extends AppCompatActivity implements View.OnClickListener {


    private Context mContext;
    private CountDown mCountDownTimer;

    private static final long MAX_TIME = 61 * 1000;


    private TextView mTimerTv;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        mContext = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_countTime_custom);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        mTimerTv = (TextView) findViewById(R.id.tv_countTime);

        initCountDownTimer();
        mCountDownTimer.start();
    }

    public void initCountDownTimer() {
        mCountDownTimer = new CountDown();
        mCountDownTimer.setMillisInFuture(MAX_TIME);
        mCountDownTimer.setCountdownInterval(1000);
        mCountDownTimer.setCountDownListener(new CountDown.CountDownListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
                mTimerTv.setText("完成!");
            }

            @Override
            public void onTick(long millisUntilFinished) {
                mTimerTv.setText(TimeTools.getCountTimeByLong(millisUntilFinished));
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                mCountDownTimer.start();
                break;
            case R.id.btn_cancel:
                mCountDownTimer.cancel();
                break;
            case R.id.btn_pause:
                mCountDownTimer.pause();
                break;
            case R.id.btn_resume:
                mCountDownTimer.resume();
                break;
            default:
                break;
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
