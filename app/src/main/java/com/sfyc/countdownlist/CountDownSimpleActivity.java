package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sfyc.countdownlist.utils.TimeTools;

/**
 * Author :leilei on 2016/11/11 2300.
 */
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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_countdown_user);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        mTimerTv = (TextView) findViewById(R.id.tv_countTime);

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

            public void onFinish() {
                mTimerTv.setText("完成!");
            }
        };
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                isPause = false;
                mCountDownTimer.start();
                break;
            case R.id.btn_cancel:
                isPause = false;
                mCountDownTimer.cancel();
                break;
            case R.id.btn_pause:
                if (!isPause) {
                    isPause = true;
                    mCountDownTimer.cancel();
                }
                break;
            case R.id.btn_resume:
                if (curTime != 0 && isPause) {
                    //将上次当前剩余时间作为新的时长
                    initCountDownTimer(curTime);
                    mCountDownTimer.start();
                    isPause = false;
                }
                break;
            default:
                break;
        }
    }

}
