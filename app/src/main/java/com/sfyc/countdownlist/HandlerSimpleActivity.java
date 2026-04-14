package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sfyc.countdownlist.utils.TimeTools;

public class HandlerSimpleActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;

    private static final long MAX_TIME = 61 * 1000;
    private long curTime = 0;
    private boolean isPause = false;

    private TextView mTimerTv;
    private Toolbar mToolbar;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            curTime -= 1000;
            mTimerTv.setText(TimeTools.getCountTimeByLong(curTime));
            if (curTime > 0) {
                mHandler.postDelayed(this, 1000);
            } else {
                Toast.makeText(mContext, "Finished", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        mContext = this;
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_hanlder_user);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        mTimerTv = findViewById(R.id.tv_countTime);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_start) {
            curTime = MAX_TIME;
            isPause = false;
            mHandler.removeCallbacks(runnable);
            mHandler.postDelayed(runnable, 1000);
        } else if (viewId == R.id.btn_cancel) {
            isPause = false;
            mHandler.removeCallbacks(runnable);
        } else if (viewId == R.id.btn_pause) {
            if (!isPause) {
                isPause = true;
                mHandler.removeCallbacks(runnable);
            }
        } else if (viewId == R.id.btn_resume) {
            if (isPause && curTime > 0) {
                isPause = false;
                mHandler.removeCallbacks(runnable);
                mHandler.postDelayed(runnable, 1000);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
    }
}
