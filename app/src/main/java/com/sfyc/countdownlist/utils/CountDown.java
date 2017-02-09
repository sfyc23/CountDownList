package com.sfyc.countdownlist.utils;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;


/**
 * Author :leilei on 2017/2/8 1935.
 * start()、 cancel()、 pause()、resume()
 */
public class CountDown {
    private long mStopTimeInFuture;
    private static final int MSG = 1;

    //是否取消
    private boolean mCancelled = false;
    private long mMillisInFuture;
    //跳转时间间隔
    private long mCountdownInterval;

    //暂停时，当时剩余时间
    private long mCurrentMillisLeft;
    private boolean mPause = false;

    private CountDownListener mCountDownListener;

    public CountDown() {
    }

    public CountDown(long millisInFuture, long countdownInterval) {
        this.mMillisInFuture = millisInFuture;
        this.mCountdownInterval = countdownInterval;
    }

    public CountDown(long millisInFuture, long countdownInterval, CountDownListener countDownListener) {
        this.mMillisInFuture = millisInFuture;
        this.mCountdownInterval = countdownInterval;
        this.mCountDownListener = countDownListener;
    }

    public synchronized final void start() {
        if (mMillisInFuture <= 0 && mCountdownInterval <= 0) {
            throw new RuntimeException("you must set the millisInFuture > 0 or countdownInterval >0");
        }
        mCancelled = false;
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mPause = false;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
    }

    public synchronized final void cancel() {
        if (mHandler != null) {
            mPause = false;
            mHandler.removeMessages(MSG);
        }
    }

    /**
     * 按一下暂停，再按一下继续倒计时
     */
    public synchronized final void pause() {
        if (mHandler != null) {
            if (mCancelled) {
                return;
            }
            if (mCurrentMillisLeft < mCountdownInterval) {
                return;
            }
            if (!mPause) {
                mHandler.removeMessages(MSG);
                mPause = true;
            }
        }

    }

    /**
     * 重新开始
     */
    public void resume() {
        if (mMillisInFuture <= 0 && mCountdownInterval <= 0) {
            throw new RuntimeException("you must set the millisInFuture > 0 or countdownInterval >0");
        }
        if (mCancelled) {
            return;
        }
        //剩余时长少于
        if (mCurrentMillisLeft < mCountdownInterval || !mPause) {
            return;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mCurrentMillisLeft;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        mPause = false;

    }

    public void restart() {
        if (mMillisInFuture <= 0 && mCountdownInterval <= 0) {
            throw new RuntimeException("you must set the millisInFuture > 0 or countdownInterval >0");
        }
        mCancelled = false;
        mPause = false;
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
    }


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            synchronized (CountDown.this) {
                if (mCancelled) {
                    return;
                }
                //剩余毫秒数
                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                if (millisLeft <= 0) {
                    mCurrentMillisLeft = 0;
                    if (mCountDownListener != null) {
                        mCountDownListener.onFinish();
                    }
                } else if (millisLeft < mCountdownInterval) {
                    mCurrentMillisLeft = 0;
                    // 剩余时间小于一次时间间隔的时候，不再通知，只是延迟一下
                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
                } else {//有多余的时间
                    long lastTickStart = SystemClock.elapsedRealtime();
                    if (mCountDownListener != null) {
                        mCountDownListener.onTick(millisLeft);
                    }
                    mCurrentMillisLeft = millisLeft;
                    // 考虑用户的onTick需要花费时间,处理用户onTick执行的时间
                    long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();

                    // 特殊情况：用户的onTick方法花费的时间比interval长，那么直接跳转到下一次interval
                    while (delay < 0) delay += mCountdownInterval;

                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };


    public void setMillisInFuture(long millisInFuture) {
        this.mMillisInFuture = millisInFuture;
    }

    public void setCountdownInterval(long countdownInterval) {
        this.mCountdownInterval = countdownInterval;
    }

    public void setCountDownListener(CountDownListener countDownListener) {
        this.mCountDownListener = countDownListener;
    }

    /**
     * 倒计时监听器
     */
    public interface CountDownListener {
        public void onStart();//当倒计时开始

        public void onFinish();//当倒计时结束

        /**
         * @param millisUntilFinished 剩余时间
         */
        public void onTick(long millisUntilFinished);
    }
}
