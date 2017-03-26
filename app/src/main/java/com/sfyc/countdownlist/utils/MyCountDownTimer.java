package com.sfyc.countdownlist.utils;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Author :leilei on 2017/2/8 2020.
 * 这个是对 CountDownTimer 原代码的解析
 */
public abstract class MyCountDownTimer {

    private long mStopTimeInFuture;

    private static final int MSG = 1;
    /**
     * 是否取消
     */
    private boolean mCancelled = false;
    private long mMillisInFuture;
    private long mCountdownInterval;

    public abstract void onTick(long millisUntilFinished);
    public abstract void onFinish();

    public MyCountDownTimer(long millisInFuture, long countDownInterval) {
        this.mMillisInFuture = millisInFuture;
        this.mCountdownInterval = countDownInterval;
    }
    public synchronized final MyCountDownTimer start() {
        mCancelled = false;
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        /**
         * SystemClock.elapsedRealtime
         * 返回系统启动到现在的时间，包含设备深度休眠的时间。
         * 该时钟被保证是单调的，即使CPU在省电模式下，该时间也会继续计时。
         * 该时钟可以被使用在当测量时间间隔可能跨越系统睡眠的时间段。
         */
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (MyCountDownTimer.this) {
                if (mCancelled) {
                    return;
                }

                //剩余毫秒数
                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                if (millisLeft <= 0) {
                    onFinish();
                } else if (millisLeft < mCountdownInterval) {
                    // 剩余时间小于一次时间间隔的时候，不再通知，只是延迟一下
                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
                } else {//有多余的时间
                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(millisLeft);

                    // 处理用户onTick执行的时间？
                    long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();

                    // 特殊情况：用户的onTick方法花费的时间比interval长，那么直接跳转到下一次interval
                    while (delay < 0) delay += mCountdownInterval;

                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };
    public void cancel() {
        mHandler.removeMessages(MSG);
    }

}
