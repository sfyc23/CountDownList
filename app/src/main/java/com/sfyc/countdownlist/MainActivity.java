package com.sfyc.countdownlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sfyc.countdownlist.listview.CountDownListActivity;
import com.sfyc.countdownlist.listview.CountDownRecyclerViewActivity;
import com.sfyc.countdownlist.sms.SmsKotilnActivity;
import com.sfyc.countdownlist.sms.SmsCountDownButtonActivity;
import com.sfyc.countdownlist.sms.SmsHandlerActivity;
import com.sfyc.countdownlist.sms.SmsRxJavaActivity;
import com.sfyc.countdownlist.sms.SmsRxbindingActivity;

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
            R.id.btn_valueAnimator,
            R.id.btn_rxJava,
            R.id.btn_chronomete,
            R.id.btn_alarm,
            R.id.btn_list_list,
            R.id.btn_list_recycler,
            R.id.btn_countTime_custom,
            R.id.btn_sms_countDownButton,
            R.id.btn_sms_handlerUtil,
            R.id.btn_sms_rxbinding,
            R.id.btn_sms_kotiln,
            R.id.btn_sms_rxJava

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
            case R.id.btn_rxJava:
                startActivity(new Intent(this, RxJavaActivity.class));
                break;
            case R.id.btn_chronomete:
                startActivity(new Intent(this, ChronometerActivity.class));
                break;
            case R.id.btn_alarm:
                startActivity(new Intent(this, AlarmActivity.class));
                break;
            case R.id.btn_list_list:
                startActivity(new Intent(this, CountDownListActivity.class));
                break;
            case R.id.btn_list_recycler:
                startActivity(new Intent(this, CountDownRecyclerViewActivity.class));
                break;

            case R.id.btn_countTime_custom:
                startActivity(new Intent(this, CountDownCustomActivity.class));
                break;

            case R.id.btn_sms_handlerUtil:
                startActivity(new Intent(this, SmsHandlerActivity.class));
                break;
            case R.id.btn_sms_countDownButton:
                startActivity(new Intent(this, SmsCountDownButtonActivity.class));
                break;
            case R.id.btn_sms_rxJava:
                startActivity(new Intent(this, SmsRxJavaActivity.class));
                break;
            case R.id.btn_sms_rxbinding:
                startActivity(new Intent(this, SmsRxbindingActivity.class));
                break;
            case R.id.btn_sms_kotiln:
                startActivity(new Intent(this, SmsKotilnActivity.class));
                break;
            default:
                break;
        }
    }
}
