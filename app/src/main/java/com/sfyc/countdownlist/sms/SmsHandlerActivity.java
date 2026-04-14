package com.sfyc.countdownlist.sms;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sfyc.countdownlist.R;
import com.sfyc.countdownlist.utils.CountDownSmsUtil;

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

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_sms_hanlderUtil);

        mSendMsmBtn = findViewById(R.id.btn_send_sms);
        mSendMsmBtn.setOnClickListener(this);
        countDown = new CountDownSmsUtil(this, mSendMsmBtn, "%ss", 10);
        countDown.setCountdownListener(new CountDownSmsUtil.CountdownListener() {
            @Override
            public void onStart() {
                Toast.makeText(mContext, "SMS sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "finished");
            }

            @Override
            public void onUpdate(int currentRemainingSeconds) {
                Log.i(TAG, String.valueOf(currentRemainingSeconds));
            }
        });
        findViewById(R.id.btn_sms_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_send_sms) {
            countDown.start();
        } else if (viewId == R.id.btn_sms_submit && countDown != null) {
            countDown.onDestroy();
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
