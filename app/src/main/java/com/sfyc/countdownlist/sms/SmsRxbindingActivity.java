package com.sfyc.countdownlist.sms;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sfyc.countdownlist.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SmsRxbindingActivity extends AppCompatActivity {
    private static final String TAG = "SmsRxbindingActivity";

    private Context mContext;
    private Toolbar mToolbar;
    private TextView mBtnSendMsm;
    private Button mBtnClean;
    private EditText mEtPhone;

    private static final long MAX_COUNT_TIME = 10;

    private Observable<Long> mObservableCountTime;
    private Consumer<Long> mConsumerCountTime;
    private Disposable mDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        mContext = this;
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_sms_rxbinding);

        mBtnSendMsm = findViewById(R.id.btn_send_sms);
        mBtnClean = findViewById(R.id.btn_sms_submit);
        mEtPhone = findViewById(R.id.et_phone);

        mBtnSendMsm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCountdown();
            }
        });
        mBtnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCountdown();
            }
        });

        initCountDownObservable();
    }

    private void initCountDownObservable() {
        mObservableCountTime = Observable.just(true)
                .flatMap(new Function<Boolean, Observable<Long>>() {
                    @Override
                    public Observable<Long> apply(Boolean ignored) {
                        if (TextUtils.isEmpty(mEtPhone.getText().toString())) {
                            Toast.makeText(mContext, "phone can not be empty", Toast.LENGTH_SHORT).show();
                            return Observable.empty();
                        }
                        mBtnSendMsm.setEnabled(false);
                        mBtnSendMsm.setText(MAX_COUNT_TIME + "s");
                        return Observable.interval(1, TimeUnit.SECONDS, Schedulers.computation())
                                .take(MAX_COUNT_TIME)
                                .map(new Function<Long, Long>() {
                                    @Override
                                    public Long apply(Long aLong) {
                                        Log.d(TAG, "map thread is : " + Thread.currentThread().getName());
                                        return MAX_COUNT_TIME - (aLong + 1);
                                    }
                                });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        mConsumerCountTime = new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {
                Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
                if (aLong == 0) {
                    resetButtonState();
                } else {
                    mBtnSendMsm.setText(aLong + "s");
                }
            }
        };
    }

    private void startCountdown() {
        resetCountdown();
        mDisposable = mObservableCountTime.subscribe(
                mConsumerCountTime,
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e(TAG, "countdown failed", throwable);
                        resetButtonState();
                    }
                }
        );
    }

    private void resetCountdown() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        resetButtonState();
    }

    private void resetButtonState() {
        mBtnSendMsm.setEnabled(true);
        mBtnSendMsm.setText(R.string.sms_send_code);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetCountdown();
    }
}
