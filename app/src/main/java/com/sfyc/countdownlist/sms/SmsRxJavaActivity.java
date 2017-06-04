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

    private TextView mBtnSendMsm;

    private Context mContext;

    private Toolbar mToolbar;

    private static final long MAX_COUNT_TIME = 10;

    private Disposable mDisposable;
    private Observer mObserver;
    private Observable mObservable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        mContext = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_sms_rxjava);

        mBtnSendMsm = (TextView) findViewById(R.id.btn_send_sms);
        mBtnSendMsm.setOnClickListener(this);
        findViewById(R.id.btn_sms_submit).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send_sms:
                if (mObservable == null) {
                    initCountDown();
                    mObservable.subscribe(mObserver);
                } else {
                    mObservable.subscribe(mObserver);
                }

                break;
            case R.id.btn_sms_submit:
                if (mDisposable != null) {
                    mDisposable.dispose();
                    mBtnSendMsm.setEnabled(true);
                    mBtnSendMsm.setText("发送短信");
                }
                break;
            default:
                break;
        }

    }



    public void initCountDown() {
        /**
         * RxJava 方式实现
         */
        //它在指定延迟之后先发射一个零值，然后再按照指定的时间间隔发射递增的数字,设置0延迟，每隔1000毫秒发送一条数据
        mObservable = Observable.interval(1, TimeUnit.SECONDS)
                .take(MAX_COUNT_TIME + 1)//设置总共发送的次数,续1s
                .map(new Function<Long, Long>() {//数据转换 long 值是从0到最大，倒计时需要将值倒置
                    @Override
                    public Long apply(Long aLong) throws Exception {//已经过了一秒
                        return MAX_COUNT_TIME - aLong - 1;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .doOnSubscribe(new Consumer<Disposable>() {//执行计时任务前先将 button 设置为不可点击
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mBtnSendMsm.setEnabled(false);
                        mBtnSendMsm.setText(MAX_COUNT_TIME + "s");
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
                mBtnSendMsm.setText(value + "s");
                Log.e(TAG, "value : " + value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                mBtnSendMsm.setEnabled(true);
                mBtnSendMsm.setText("发送短信");
            }
        };

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
