package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Author :leilei on 2016/11/11 2300.
 */
public class CountDownSimpleActivity extends AppCompatActivity implements View.OnClickListener {

    public TextView mTvCountDownTime;
    public Button btnStart, btnCancel;

    private Context mContext;
    private Toolbar mToolbar;

    private final static int BEGIN_COUNT_TIME = 62 * 1000;

    private CountDownTimer mCountDownTimer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        mContext = this;
        btnStart = (Button) findViewById(R.id.btn_start);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnStart.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        //倒计时30秒，每秒进行一下计时
        mCountDownTimer = new CountDownTimer(30 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTvCountDownTime.setText("剩余时间: " + millisUntilFinished / 1000 + "秒");

            }

            public void onFinish() {
                mTvCountDownTime.setText("完成!");
            }
        };


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
                break;
            default:
                break;
        }
    }

    public void showToastMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
