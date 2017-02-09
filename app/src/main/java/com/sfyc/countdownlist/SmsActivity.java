package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sfyc.countdownlist.utils.CountDownSmsUtils;

/**
 * Author :leilei on 2017/2/8 1806.
 * CountDownUtils 短信倒计时使用
 */
public class SmsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SmsActivity";
    private CountDownSmsUtils countDown;
    private TextView mSendMsmTv;

    private Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        mContext = this;
        mSendMsmTv = (TextView) findViewById(R.id.tv_send_sms);
        mSendMsmTv.setOnClickListener(this);
        countDown = new CountDownSmsUtils(mSendMsmTv, "%s秒");
//        countDown = new CountDownUtils(mSendMsmTv, "%s秒",15);
//        countDown.start();
        countDown.setCountdownListener(new CountDownSmsUtils.CountdownListener() {
            @Override
            public void onStart() {
                Toast.makeText(mContext,"已发送短信",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Toast.makeText(mContext,"结束",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpdate(int currentRemainingSeconds) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        countDown.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDown != null) {
            countDown.stop();
        }
    }
}
