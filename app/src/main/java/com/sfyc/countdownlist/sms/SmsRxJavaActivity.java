package com.sfyc.countdownlist.sms;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sfyc.countdownlist.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SmsRxJavaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SmsRxJavaActivity";

    private TextView mBtnSendMsm;
    private Context mContext;
    private Toolbar mToolbar;

    private static final long MAX_COUNT_TIME = 10;

    private Disposable mDisposable;
    private Observer<Long> mObserver;
    private Observable<Long> mObservable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        mContext = this;

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_sms_rxjava);

        mBtnSendMsm = findViewById(R.id.btn_send_sms);
        mBtnSendMsm.setOnClickListener(this);
        findViewById(R.id.btn_sms_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_send_sms) {
            if (mObservable == null) {
                initCountDown();
            }
            mObservable.subscribe(mObserver);
        } else if (viewId == R.id.btn_sms_submit) {
            if (mDisposable != null) {
                mDisposable.dispose();
                mBtnSendMsm.setEnabled(true);
                mBtnSendMsm.setText(R.string.sms_send_code);
            }
        }
    }

    public void initCountDown() {
        mObservable = Observable.interval(1, TimeUnit.SECONDS)
                .take(MAX_COUNT_TIME + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) {
                        return MAX_COUNT_TIME - aLong - 1;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        mBtnSendMsm.setEnabled(false);
                        mBtnSendMsm.setText(MAX_COUNT_TIME + "s");
                        Log.e(TAG, "disposable");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

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
                Log.e(TAG, "countdown failed", e);
            }

            @Override
            public void onComplete() {
                mBtnSendMsm.setEnabled(true);
                mBtnSendMsm.setText(R.string.sms_send_code);
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
