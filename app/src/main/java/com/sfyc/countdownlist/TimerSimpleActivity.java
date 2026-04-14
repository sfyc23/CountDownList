package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sfyc.countdownlist.utils.TimeTools;

import java.util.Timer;
import java.util.TimerTask;

public class TimerSimpleActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int WHAT = 101;
    private Context mContext;
    private TextView mTimerTv;

    private Timer mTimer;
    private TimerTask mTimerTask;

    private static final long MAX_TIME = 12000;
    private long curTime = 0;
    private boolean isPause = false;

    private Toolbar mToolbar;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT) {
                long remain = (long) msg.obj;
                mTimerTv.setText(TimeTools.getCountTimeByLong(remain));
                if (remain <= 0 && mTimer != null) {
                    mTimer.cancel();
                    curTime = 0;
                    Toast.makeText(mContext, "Finished", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        mContext = this;
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_timer_user);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        mTimerTv = findViewById(R.id.tv_countTime);

        initTimer();
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    public void initTimer() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (curTime == 0) {
                    curTime = MAX_TIME;
                } else {
                    curTime -= 1000;
                }
                Message message = Message.obtain();
                message.what = WHAT;
                message.obj = curTime;
                mHandler.sendMessage(message);
            }
        };
        mTimer = new Timer();
    }

    public void destroyTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_start) {
            destroyTimer();
            initTimer();
            isPause = false;
            mTimer.schedule(mTimerTask, 0, 1000);
        } else if (viewId == R.id.btn_cancel) {
            if (curTime != 0) {
                curTime = 0;
                isPause = false;
                mTimer.cancel();
            }
        } else if (viewId == R.id.btn_pause) {
            if (curTime != 0 && !isPause) {
                isPause = true;
                mTimer.cancel();
            }
        } else if (viewId == R.id.btn_resume && curTime != 0 && isPause) {
            destroyTimer();
            initTimer();
            mTimer.schedule(mTimerTask, 0, 1000);
            isPause = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyTimer();
        mHandler.removeMessages(WHAT);
    }
}
