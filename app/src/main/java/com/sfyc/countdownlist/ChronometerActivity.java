package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author :leilei on 2016/11/11 2300.
 * ValueAnimator 动画 实现 倒计时
 */
public class ChronometerActivity extends AppCompatActivity {


    private Context mContext;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //    private static final long MAX_TIME = 66 * 1000;
    private static final long MAX_TIME = 12 * 1000;

    @BindView(R.id.chronometer)
    Chronometer chronometer;

    float mCurrentValue = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chronometer);
        ButterKnife.bind(this);
        mContext = this;
        toolbar.setTitle(R.string.title_valueAnimatior_user);
        initValueAnimator();


//        mAnimator.start();
        chronometer.setBase(SystemClock.elapsedRealtime());
        Log.e("TAG", SystemClock.elapsedRealtime() + "");
        chronometer.start();
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                Log.e("TAG",chronometer.getContentDescription().toString());
            }
        });
    }


    public void initValueAnimator() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mAnimator != null) {
//            mAnimator.cancel();
//        }
    }

    @OnClick({R.id.btn_start, R.id.btn_cancel, R.id.btn_pause, R.id.btn_resume})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
//                mAnimator.start();
                chronometer.start();
                break;
            case R.id.btn_cancel:
//                mAnimator.cancel();
                chronometer.stop();
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
