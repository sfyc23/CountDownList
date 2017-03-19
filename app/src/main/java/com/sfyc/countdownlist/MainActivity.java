package com.sfyc.countdownlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar.setTitle("我的倒计时");
    }

    @OnClick({
            R.id.btn_timer,
            R.id.btn_hanlder,
            R.id.btn_countTime,
            R.id.btn_countTime_custom,
            R.id.btn_valueAnimator,
            R.id.btn_sms
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_timer:
                startActivity(new Intent(this, TimerSimpleActivity.class));
                break;
            case R.id.btn_hanlder:
                startActivity(new Intent(this, HandlerSimpleActivity.class));
                break;
            case R.id.btn_countTime:
                startActivity(new Intent(this, CountDownSimpleActivity.class));
                break;
            case R.id.btn_valueAnimator:
                startActivity(new Intent(this, ValueAnimatorActivity.class));
                break;


            case R.id.btn_countTime_custom:
                startActivity(new Intent(this, CountDownCustomActivity.class));
                break;
            case R.id.btn_sms:
                startActivity(new Intent(this, SmsActivity.class));
                break;

            default:
                break;
        }
    }
}
