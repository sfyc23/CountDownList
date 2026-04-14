package com.sfyc.countdownlist;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sfyc.countdownlist.utils.TimeTools;

public class ValueAnimatorActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTimerTv;
    private ProgressBar pbVa;
    private TextView tvVaPercent;
    private Toolbar toolbar;
    private Context mContext;

    private static final long MAX_TIME = 12 * 1000;

    private ValueAnimator mAnimator;
    private float mCurrentValue = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value_anim);
        mContext = this;
        mTimerTv = findViewById(R.id.tv_countTime);
        pbVa = findViewById(R.id.pb_va);
        tvVaPercent = findViewById(R.id.tv_va_percent);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_valueAnimatior_user);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
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
                mTimerTv.setText(TimeTools.getCountTimeByLong((long) (mCurrentValue * MAX_TIME)));
                pbVa.setProgress((int) (100 * (1 - mCurrentValue)));
                tvVaPercent.setText((int) ((1 - mCurrentValue) * 100) + "%");
            }
        });
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTimerTv.setText(TimeTools.getCountTimeByLong(MAX_TIME));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Toast.makeText(mContext, "Time finished", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Toast.makeText(mContext, "Time canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Toast.makeText(mContext, "Restart", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_start) {
            mAnimator.start();
            Log.e("ValueAnimatorActivity", "restart animator");
        } else if (viewId == R.id.btn_cancel) {
            mAnimator.cancel();
        }
    }
}
