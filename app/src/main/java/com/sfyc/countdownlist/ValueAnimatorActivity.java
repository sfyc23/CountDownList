package com.sfyc.countdownlist;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sfyc.countdownlist.utils.TimeTools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author :leilei on 2016/11/11 2300.
 * ValueAnimator 动画 实现 倒计时
 */
public class ValueAnimatorActivity extends AppCompatActivity {

    @BindView(R.id.tv_countTime)
    TextView mTimerTv;
    @BindView(R.id.pb_va)
    ProgressBar pbVa;
    @BindView(R.id.tv_va_percent)
    TextView tvVaPercent;
    private Context mContext;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //    private static final long MAX_TIME = 66 * 1000;
    private static final long MAX_TIME = 12 * 1000;


    private ValueAnimator mAnimator;
    float mCurrentValue = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value_anim);
        ButterKnife.bind(this);
        mContext = this;
        toolbar.setTitle(R.string.title_valueAnimatior_user);
        initValueAnimator();
        mAnimator.start();
    }


    public void initValueAnimator() {
        mAnimator = ValueAnimator.ofFloat(1, 0);
        mAnimator.setDuration(MAX_TIME);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentValue = (float) animation.getAnimatedValue();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTimerTv.setText(TimeTools.getCountTimeByLong((long) (mCurrentValue * MAX_TIME)));
                        pbVa.setProgress((int) (100 * (1 - mCurrentValue)));
                        tvVaPercent.setText((int) ((1 - mCurrentValue) * 100) + "%");
                    }
                });
            }

        });
        mAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mTimerTv.setText(TimeTools.getCountTimeByLong(MAX_TIME));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Toast.makeText(mContext, "时间结束", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Toast.makeText(mContext, "时间取消", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Toast.makeText(mContext, "重开", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    @OnClick({R.id.btn_start, R.id.btn_cancel, R.id.btn_pause, R.id.btn_resume})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                mAnimator.start();
                Log.e("ValueAnimatorActivity", "why");
                break;
            case R.id.btn_cancel:
                mAnimator.cancel();
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
