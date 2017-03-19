package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sfyc.countdownlist.utils.TimeTools;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Author :leilei on 2017/2/8 0714.
 */
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
    //    private int
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        mContext = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_timer_user);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        mTimerTv = (TextView) findViewById(R.id.tv_countTime);

        initTimer();
        // 参数：0，延时0秒后执行;1000，每隔1秒执行1次task。
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    /**
     * 初始化Timer
     */
    public void initTimer() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (curTime == 0) {
                    curTime = MAX_TIME;
                } else {
                    curTime -= 1000;
                }
                Message message = new Message();
                message.what = WHAT;
                message.obj = curTime;
                mHandler.sendMessage(message);
            }
        };
        mTimer = new Timer();
    }

    /**
     * destory上次使用的
     */
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

    //    start,cancel,pause,resume
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:

                destroyTimer();
                initTimer();
                isPause = false;
                mTimer.schedule(mTimerTask, 0, 1000);

                break;
            case R.id.btn_cancel:
                //如果 curTime == 0，则不需要执行此操
                if (curTime == 0) {
                    break;
                }
                curTime = 0;
                isPause = false;
                mTimer.cancel();
                break;
            case R.id.btn_pause:
                //如果 curTime == 0，则不需要执行此操
                if (curTime == 0) {
                    break;
                }
                if (!isPause) {
                    isPause = true;
                    mTimer.cancel();
                }
                break;

            case R.id.btn_resume:
                //已经结束或者还没有开始时。或者按了暂停标记。
                if (curTime != 0 && isPause) {
                    destroyTimer();
                    initTimer();
                    mTimer.schedule(mTimerTask, 0, 1000);
                    isPause = false;
                }
                break;
            default:
                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT:
                    long sRecLen = (long) msg.obj;
                    //毫秒换成00:00:00格式的方式，自己写的。
                    mTimerTv.setText(TimeTools.getCountTimeByLong(sRecLen));
                    if (sRecLen <= 0) {
                        mTimer.cancel();
                        curTime = 0;
                        Toast.makeText(mContext, "结束", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyTimer();
        if (mHandler != null) {
            mHandler.removeMessages(WHAT);
            mHandler = null;
        }
    }
}
