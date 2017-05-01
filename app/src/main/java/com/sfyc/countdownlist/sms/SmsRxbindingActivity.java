package com.sfyc.countdownlist.sms;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.sfyc.countdownlist.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Author :leilei on 2017/5/1 23:43
 * 用RxbindingA 实现短信倒计时
 */
public class SmsRxbindingActivity extends AppCompatActivity {
    private static final String TAG = "SmsRxbindingActivity";

    private TextView mSendMsmTv;

    private Context mContext;

    private Toolbar mToolbar;

    private Disposable mDisposable;
    private long MAX_TIME = 10;
    private Observable<Object> verifyCodeObservable;

    private Button mBtnClean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        mContext = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_sms_rxbinding);

        mSendMsmTv = (TextView) findViewById(R.id.tv_send_sms);

        mBtnClean = (Button) findViewById(R.id.btn_sms_submit);
        initSendMsm();

        RxView.clicks(mBtnClean).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (mDisposable != null) {
                    mDisposable.dispose();
                    mSendMsmTv.setEnabled(true);
                    mSendMsmTv.setText("发送短信");
                }
            }
        });

    }


    public void initSendMsm() {
        verifyCodeObservable = RxView.clicks(mSendMsmTv).throttleFirst(MAX_TIME, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        RxView.enabled(mSendMsmTv).accept(true);
                        RxTextView.text(mSendMsmTv).accept("发送短信");
                    }
                });

        verifyCodeObservable.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                        .take(MAX_TIME)
                        .subscribe(new Observer<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                mDisposable = d;
                            }

                            @Override
                            public void onNext(Long value) {
                                try {
                                    RxTextView.text(mSendMsmTv).accept("剩余" + (MAX_TIME - value) + "秒");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                try {
                                    RxView.enabled(mSendMsmTv).accept(true);
                                    RxTextView.text(mSendMsmTv).accept("发送短信");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        if (verifyCodeObservable != null) {
            verifyCodeObservable.unsubscribeOn(AndroidSchedulers.mainThread());
        }


    }


}
