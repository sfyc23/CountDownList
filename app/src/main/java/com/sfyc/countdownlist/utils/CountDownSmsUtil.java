package com.sfyc.countdownlist.utils;

import android.app.Activity;
import android.os.Handler;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * 倒计时器类
 */
public class CountDownSmsUtil implements Runnable {
    //默认倒计时60s
    private int remainingSeconds = 60;
    private int currentRemainingSeconds;
    private boolean running;
    private String defaultText;
    private String countdownText;
    private TextView showTextView;
    private Handler handler;
    private CountdownListener countdownListener;

    private WeakReference<Activity> mActivity;

    /**
     * 创建一个倒计时器
     *
     * @param showTextView     显示倒计时的文本视图
     * @param countdownText    倒计时中显示的内容，例如："%s秒后重新获取验证码"，在倒计时的过程中会用剩余描述替换%s
     * @param remainingSeconds 倒计时秒数，例如：60，就是从60开始倒计时一直到0结束
     */
    public CountDownSmsUtil(Activity activity, TextView showTextView, String countdownText,
                            int remainingSeconds) {
        this.mActivity = new WeakReference<Activity>(activity);
        this.showTextView = showTextView;
        this.countdownText = countdownText;
        this.remainingSeconds = remainingSeconds;
        this.handler = new Handler();

    }

    /**
     * 创建一个倒计时器，默认60秒
     *
     * @param showTextView  显示倒计时的文本视图
     * @param countdownText 倒计时中显示的内容，例如："%s秒后重新获取验证码"，在倒计时的过程中会用剩余描述替换%s
     */
    public CountDownSmsUtil(Activity activity, TextView showTextView, String countdownText) {
        this(activity, showTextView, countdownText, 60);
    }


    private TextView getShowTextView() {
        if (showTextView != null) {
            return showTextView;
        }
        return null;
    }

    //判断Activity是否还存在防止出现内存泄露
    private boolean isUserRun() {
        if (mActivity == null) {
            return false;
        }
        Activity activity = mActivity.get();
        if (activity == null) {
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        if (currentRemainingSeconds > 0) {
            if (!isUserRun()) {
                stop();
                return;
            }
            getShowTextView().setEnabled(false);
            getShowTextView().setText(
                    String.format(countdownText, currentRemainingSeconds));
            if (countdownListener != null) {
                countdownListener.onUpdate(currentRemainingSeconds);
            }
            currentRemainingSeconds--;
            handler.postDelayed(this, 1000);
        } else {
            onDestroy();
        }
    }

    public void start() {
        if (!isUserRun()) {
            onDestroy();
            return;
        }
        defaultText = getShowTextView().getText().toString();
        currentRemainingSeconds = remainingSeconds;
        handler.removeCallbacks(this);
        handler.post(this);
        if (countdownListener != null) {
            countdownListener.onStart();
        }
        running = true;
    }

    private void stop() {
        getShowTextView().setEnabled(true);
        getShowTextView().setText(defaultText);
        handler.removeCallbacks(this);
        if (countdownListener != null) {
            countdownListener.onFinish();
        }
        running = false;
    }

    public void onDestroy() {
        getShowTextView().setEnabled(true);
        getShowTextView().setText(defaultText);
        handler.removeCallbacks(this);
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public String getCountdownText() {
        return countdownText;
    }

    public void setCountdownText(String countdownText) {
        this.countdownText = countdownText;
    }

    public void setCurrentRemainingSeconds(int currentRemainingSeconds) {
        this.currentRemainingSeconds = currentRemainingSeconds;
    }

    public void setCountdownListener(CountdownListener countdownListener) {
        this.countdownListener = countdownListener;
    }

    /**
     * 倒计时监听器
     */
    public interface CountdownListener {
        /**
         * 当倒计时开始调用
         */
        public void onStart();
        /**
         * 当倒计时结束
         */
        public void onFinish();
        /**
         * 更新
         *
         * @param currentRemainingSeconds 剩余时间
         */
        public void onUpdate(int currentRemainingSeconds);
    }

}
