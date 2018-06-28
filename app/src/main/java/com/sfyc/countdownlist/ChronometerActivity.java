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
 * Author :leilei on 2018-6-29 0:40:13
 * Chronometer 实现倒计时 不建议使用
 */
public class ChronometerActivity extends AppCompatActivity {


    private Context mContext;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //    private static final long MAX_TIME = 66 * 1000;
    private static final long MAX_TIME = 12 * 1000;

    @BindView(R.id.chronometer)
    Chronometer mChronometer;

    float mCurrentValue = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chronometer);
        ButterKnife.bind(this);
        mContext = this;
        toolbar.setTitle(R.string.title_chronometer_user);
        initValueAnimator();


        mChronometer.setBase(SystemClock.elapsedRealtime() + MAX_TIME);

        //这个方法 在 sdk -24 才可以使用，可以来说非常不适用了
        mChronometer.setCountDown(true);
        mChronometer.start();
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                long second = time / 1000;
                if (second == 0) {
                    chronometer.stop();
                }
                Log.e("TAG", "相差时间：" + time);

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

                mChronometer.setBase(SystemClock.elapsedRealtime() + MAX_TIME);
                mChronometer.start();
                break;
            case R.id.btn_cancel:

                mChronometer.stop();
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
