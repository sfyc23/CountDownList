package com.sfyc.countdownlist.sms;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sfyc.countdownlist.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author :leilei on 2017/2/8 1806
 * 用RxJava 实现短信倒计时
 */
public class SmsRxJavaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SmsRxJavaActivity";

    private TextView mSendMsmTv;

    private Context mContext;

    private Toolbar mToolbar;

    private Disposable mDisposable;
    private Observable mObservable;
    private Observer mObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        mContext = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_sms_rxjava);

        mSendMsmTv = (TextView) findViewById(R.id.tv_send_sms);
        mSendMsmTv.setOnClickListener(this);
        findViewById(R.id.btn_sms_submit).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_send_sms:
                startCountDown();
                break;
            case R.id.btn_sms_submit:
                if (mDisposable != null) {
                    mDisposable.dispose();
                    mSendMsmTv.setEnabled(true);
                    mSendMsmTv.setText("发送短信");
                }
                break;
            default:
                break;
        }

    }

    private long MAX_TIME = 10;

    public void startCountDown() {
        /**
         * RxJava 方式实现
         */
        mObservable = Observable.interval(0, 1, TimeUnit.SECONDS)//它在指定延迟之后先发射一个零值，然后再按照指定的时间间隔发射递增的数字,设置0延迟，每隔1000毫秒发送一条数据
                .take(MAX_TIME + 1)//设置总共发送的次数
                .map(new Function<Long, Long>() {//long 值是从0到最大，倒计时需要将值倒置
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return MAX_TIME - aLong;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .doOnSubscribe(new Consumer<Disposable>() {//执行计时任务前先将 button 设置为不可点击
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mSendMsmTv.setEnabled(false);
                        Log.e(TAG, "disposable ");
                    }
                })

                .observeOn(AndroidSchedulers.mainThread());//显示放在主线程。


        mObserver = new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(Long value) {
                mSendMsmTv.setText(value + "s");
                Log.e(TAG, "value : " + value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                mSendMsmTv.setEnabled(true);
                mSendMsmTv.setText("发送短信");
            }
        };
        mObservable.subscribe(mObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
