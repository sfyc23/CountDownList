package com.sfyc.countdownlist.sms;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sfyc.countdownlist.R;
import com.sfyc.countdownlist.widget.CountDownButton;

public class SmsCountDownButtonActivity extends AppCompatActivity implements View.OnClickListener {

    private CountDownButton mCountDownButton;
    private Context mContext;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_button);
        mContext = this;

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_sms_countDownButton);

        mCountDownButton = findViewById(R.id.btn_send_sms);
        mCountDownButton.setOnClickListener(this);
        findViewById(R.id.btn_sms_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_send_sms) {
            mCountDownButton.start();
        } else if (viewId == R.id.btn_sms_submit) {
            mCountDownButton.stop();
        }
    }
}
