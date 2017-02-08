package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sfyc.countdownlist.utils.TimeTools;

/**
 * Author :leilei on 2016/11/11 2300.
 */
public class CountDownSimpleActivity extends AppCompatActivity implements View.OnClickListener {


    private Context mContext;
    private Toolbar mToolbar;

    private CountDownTimer mCountDownTimer;

    private static final long MAX_TIME = 12000;
    private long curTime = 0;
    private boolean isPause = false;

    private TextView mTimerTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        mContext = this;
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

                if (isPause && (curTime < MAX_TIME && curTime > 0)) {
                    initCountDownTimer(curTime);
                    mCountDownTimer.start();
                } else {
                    mCountDownTimer.start();
                }
                break;
            case R.id.btn_cancel:
                mCountDownTimer.cancel();
                break;
            case R.id.btn_pause:
                if (!isPause) {
                    isPause = true;
                    mCountDownTimer.cancel();
                } else {//继续播放
                    if (curTime != 0) {
                        initCountDownTimer(curTime);
                        mCountDownTimer.start();
                        isPause = false;
                    }
                }
                break;
            case R.id.btn_resume:
                mCountDownTimer.start();
                break;
            default:
                break;
        }
    }

    public void showToastMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
