package com.sfyc.countdownlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


import com.sfyc.countdownlist.listview.CountDownListActivity;
import com.sfyc.countdownlist.listview.CountDownRecyclerViewActivity;
import com.sfyc.countdownlist.listview.CountDownRecyclerViewActivity2;
import com.sfyc.countdownlist.sms.SmsCountDownButtonActivity;
import com.sfyc.countdownlist.sms.SmsHandlerActivity;
import com.sfyc.countdownlist.sms.SmsKotilnActivity;
import com.sfyc.countdownlist.sms.SmsRxJavaActivity;
import com.sfyc.countdownlist.compose.ComposeMainActivity;
import com.sfyc.countdownlist.coroutine.CoroutineListActivity;
import com.sfyc.countdownlist.coroutine.CoroutineSingleActivity;
import com.sfyc.countdownlist.coroutine.CoroutineSmsActivity;
import com.sfyc.countdownlist.flow.FlowListActivity;
import com.sfyc.countdownlist.flow.FlowSingleActivity;
import com.sfyc.countdownlist.sms.SmsRxbindingActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerClickListeners();
    }

    private void registerClickListeners() {
        int[] buttonIds = {
                R.id.btn_timer,
                R.id.btn_hanlder,
                R.id.btn_countTime,
                R.id.btn_valueAnimator,
                R.id.btn_rxJava,
                R.id.btn_chronomete,
                R.id.btn_alarm,
                R.id.btn_list_list,
                R.id.btn_list_recycler,
                R.id.btn_list_recycler02,
                R.id.btn_coroutine_single,
                R.id.btn_coroutine_list,
                R.id.btn_coroutine_sms,
                R.id.btn_flow_single,
                R.id.btn_flow_list,
                R.id.btn_motion_timer,
                R.id.btn_compose,
                R.id.btn_countTime_custom,
                R.id.btn_sms_countDownButton,
                R.id.btn_sms_handlerUtil,
                R.id.btn_sms_rxbinding,
                R.id.btn_sms_kotiln,
                R.id.btn_sms_rxJava
        };
        for (int buttonId : buttonIds) {
            findViewById(buttonId).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_timer) {
            startActivity(new Intent(this, TimerSimpleActivity.class));
        } else if (viewId == R.id.btn_hanlder) {
            startActivity(new Intent(this, HandlerSimpleActivity.class));
        } else if (viewId == R.id.btn_countTime) {
            startActivity(new Intent(this, CountDownSimpleActivity.class));
        } else if (viewId == R.id.btn_valueAnimator) {
            startActivity(new Intent(this, ValueAnimatorActivity.class));
        } else if (viewId == R.id.btn_rxJava) {
            startActivity(new Intent(this, RxJavaActivity.class));
        } else if (viewId == R.id.btn_chronomete) {
            startActivity(new Intent(this, ChronometerActivity.class));
        } else if (viewId == R.id.btn_alarm) {
            startActivity(new Intent(this, AlarmActivity.class));
        } else if (viewId == R.id.btn_list_list) {
            startActivity(new Intent(this, CountDownListActivity.class));
        } else if (viewId == R.id.btn_list_recycler) {
            startActivity(new Intent(this, CountDownRecyclerViewActivity.class));
        } else if (viewId == R.id.btn_list_recycler02) {
            startActivity(new Intent(this, CountDownRecyclerViewActivity2.class));
        } else if (viewId == R.id.btn_coroutine_single) {
            startActivity(new Intent(this, CoroutineSingleActivity.class));
        } else if (viewId == R.id.btn_coroutine_list) {
            startActivity(new Intent(this, CoroutineListActivity.class));
        } else if (viewId == R.id.btn_coroutine_sms) {
            startActivity(new Intent(this, CoroutineSmsActivity.class));
        } else if (viewId == R.id.btn_flow_single) {
            startActivity(new Intent(this, FlowSingleActivity.class));
        } else if (viewId == R.id.btn_flow_list) {
            startActivity(new Intent(this, FlowListActivity.class));
        } else if (viewId == R.id.btn_motion_timer) {
            startActivity(new Intent(this, MotionTimerActivity.class));
        } else if (viewId == R.id.btn_compose) {
            startActivity(new Intent(this, ComposeMainActivity.class));
        } else if (viewId == R.id.btn_countTime_custom) {
            startActivity(new Intent(this, CountDownCustomActivity.class));
        } else if (viewId == R.id.btn_sms_handlerUtil) {
            startActivity(new Intent(this, SmsHandlerActivity.class));
        } else if (viewId == R.id.btn_sms_countDownButton) {
            startActivity(new Intent(this, SmsCountDownButtonActivity.class));
        } else if (viewId == R.id.btn_sms_rxJava) {
            startActivity(new Intent(this, SmsRxJavaActivity.class));
        } else if (viewId == R.id.btn_sms_rxbinding) {
            startActivity(new Intent(this, SmsRxbindingActivity.class));
        } else if (viewId == R.id.btn_sms_kotiln) {
            startActivity(new Intent(this, SmsKotilnActivity.class));
        }
    }
}
