package com.sfyc.countdownlist.sms;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sfyc.countdownlist.R;
import com.sfyc.countdownlist.widget.CountDownButton;

/**
 * Author :leilei on 2017/2/8 1806.
 * CountDownUtils 短信倒计时使用
 */
public class SmsCountDownButtonActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SmsCountDownButtonActivity";

    private CountDownButton mCountDownButton;

    private Context mContext;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_button);
        mContext = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_sms_countDownButton);

        mCountDownButton = (CountDownButton) findViewById(R.id.btn_send_sms);
        mCountDownButton.setOnClickListener(this);
        findViewById(R.id.btn_sms_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send_sms:
                mCountDownButton.start();
                break;
            case R.id.btn_sms_submit:
                mCountDownButton.stop();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
