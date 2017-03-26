package com.sfyc.countdownlist.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;

import com.sfyc.countdownlist.R;

/**
 * Author :leilei on 2017/3/27 0210.
 * 短信倒计时
 */
public class CountDownButton extends AppCompatButton {

    public interface OnCountDownFinishListener {
        void onFinish();
    }

    private static final String TAG = "CountDownButton";

    private boolean mRunning = false;
    private boolean mStarted = false;

    private long mSecondInFuture = 60;
    private String mDefaultText;
    private String mFormatString;

    private CountDownTimer mCountDownTimer;
    private OnCountDownFinishListener mOnCountDownFinishListener;

    public CountDownButton(Context context) {
        this(context, null);
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CountDownButton, defStyleAttr, 0);
        mSecondInFuture = a.getInt(R.styleable.CountDownButton_secondInFuture, (int) mSecondInFuture);
        mFormatString = a.getString(R.styleable.CountDownButton_format);
        a.recycle();
    }

    public void start() {
        mStarted = true;
        updateRunning();
        Log.e(TAG, "start");
    }

    public void stop() {
        mStarted = false;
        updateRunning();
        Log.e(TAG, "stop");
    }

    private void updateRunning() {
        boolean running = mStarted;
        if (running != mRunning) {
            if (running) {
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                setEnabled(false);
                mDefaultText = getText().toString();
                mCountDownTimer = new CountDownTimer(mSecondInFuture * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        setText(String.format(mFormatString, millisUntilFinished / 1000));
                        Log.e(TAG, String.format(mFormatString, millisUntilFinished / 1000));
                    }

                    @Override
                    public void onFinish() {
                        //倒计时结束
                        mRunning = false;
                        setEnabled(true);
                        setText(mDefaultText);
                        if (mOnCountDownFinishListener != null) {
                            mOnCountDownFinishListener.onFinish();
                        }
                    }
                }.start();
            } else {
//                removeCallbacks(mTickRunnable);
                setEnabled(true);
                setText(mDefaultText);
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
            }
            mRunning = running;
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }


    public long getSecondInFuture() {
        return mSecondInFuture;
    }

    public void setSecondInFuture(long secondInFuture) {
        this.mSecondInFuture = secondInFuture;
    }


    public String getFormatString() {
        return mFormatString;
    }

    public void setFormatString(String formatString) {
        this.mFormatString = formatString;
    }

    public OnCountDownFinishListener getOnCountDownFinishListener() {
        return mOnCountDownFinishListener;
    }

    public void setOnCountDownFinishListener(OnCountDownFinishListener listener) {
        this.mOnCountDownFinishListener = listener;
    }
}
