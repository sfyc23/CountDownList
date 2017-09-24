package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sfyc.countdownlist.utils.TimeTools;

/**
 * Author :leilei on 2017/2/8 1642.
 * 用hanlder的延迟实现倒计时功能
 */
public class HandlerSimpleActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;

    private static final long MAX_TIME = 61 * 1000;
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
        mToolbar.setTitle(R.string.title_hanlder_user);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        mTimerTv = (TextView) findViewById(R.id.tv_countTime);

    }

    private void initDownTimer() {


    }
    Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            curTime -=1000;
            mTimerTv.setText(TimeTools.getCountTimeByLong(curTime));
            if (curTime > 0) {
                mHandler.postDelayed(this, 1000);
            } else {
                Toast.makeText(mContext,"运行结束",Toast.LENGTH_SHORT).show();
            }
        }
    };



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                curTime = MAX_TIME;
                mHandler.removeCallbacks(runnable);
                mHandler.postDelayed(runnable,1000);
                break;
            case R.id.btn_cancel:
                mHandler.removeCallbacks(runnable);
                break;
            case R.id.btn_pause:
                if (!isPause) {
                    mHandler.removeCallbacks(runnable);
                }
                break;
            case R.id.btn_resume:
                mHandler.removeCallbacks(runnable);
                mHandler.postDelayed(runnable, 1000);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
        }
    }
}
