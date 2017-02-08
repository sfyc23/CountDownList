package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.sfyc.countdownlist.utils.TimeTools;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author :leilei on 2016/11/11 2300.
 */
public class SimpleActivity extends AppCompatActivity {
    private static final String TAG = "SimpleActivity";
    @BindView(R.id.tv_hanlder)
    TextView mTvHanlder;

    @BindView(R.id.tv_countTime)
    TextView mTvCountDownTime;

    private Context mContext;
    private Toolbar mToolbar;

    private final static int BEGIN_COUNT_TIME = 62 * 1000;
    private MyCountDowmTime countDowmTime;
    private long currentLeftTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        mContext = this;
        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);


        countDowmTime = new MyCountDowmTime(BEGIN_COUNT_TIME);

    }

    class MyCountDowmTime extends CountDownTimer {

        public MyCountDowmTime(long millisInFuture) {
            super(millisInFuture, 2000);
        }

        @Override
        public void onTick(long timeLeft) {
            mTvCountDownTime.setText(TimeTools.getCountTimeByLong(timeLeft));
            currentLeftTime = timeLeft;
        }

        @Override
        public void onFinish() {
            mTvCountDownTime.setText("onFinish" + currentLeftTime);
        }


    }

    public void showToastMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_restart:
                countDowmTime = new MyCountDowmTime(BEGIN_COUNT_TIME);
                countDowmTime.start();
                break;
            case R.id.action_pause:
                if (countDowmTime != null) {
                    countDowmTime.cancel();
                    countDowmTime = new MyCountDowmTime(currentLeftTime);
                }
                break;
            case R.id.action_goon:
                if (countDowmTime != null) {
                    countDowmTime.start();
                }
                break;
            case R.id.action_finsh:
                if (countDowmTime != null) {
                    countDowmTime.cancel();
                    countDowmTime = null;
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDowmTime != null) {
//            countDowmTime.onFinish();
            countDowmTime.cancel();
            countDowmTime = null;
        }
    }
}
