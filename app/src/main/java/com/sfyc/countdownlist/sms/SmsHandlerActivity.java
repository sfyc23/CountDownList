package com.sfyc.countdownlist.sms;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sfyc.countdownlist.R;
import com.sfyc.countdownlist.utils.CountDownSmsUtil;

/**
 * Author :leilei on 2017/2/8 1806.
 * CountDownUtils 短信倒计时使用
 */
public class SmsHandlerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SmsActivity";
    private CountDownSmsUtil countDown;
    private Button mSendMsmBtn;

    private Context mContext;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        mContext = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_sms_hanlderUtil);

        mSendMsmBtn = (Button) findViewById(R.id.btn_send_sms);
        mSendMsmBtn.setOnClickListener(this);
//        countDown = new CountDownSmsUtil(this, mSendMsmTv, "%s秒");
        countDown = new CountDownSmsUtil(this, mSendMsmBtn, "%s秒", 10);
//        countDown.start();
        countDown.setCountdownListener(new CountDownSmsUtil.CountdownListener() {
            @Override
            public void onStart() {
                Toast.makeText(mContext, "已发送短信", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "结束");
            }

            @Override
            public void onUpdate(int currentRemainingSeconds) {
                Log.i(TAG, "" + currentRemainingSeconds);
            }
        });
        findViewById(R.id.btn_sms_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send_sms:
                countDown.start();
                break;
            case R.id.btn_sms_submit:
                if (countDown != null) {
                    countDown.onDestroy();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDown != null) {
            countDown.onDestroy();
        }
    }
}
