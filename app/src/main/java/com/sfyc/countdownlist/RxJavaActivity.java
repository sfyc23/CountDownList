package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sfyc.countdownlist.utils.TimeTools;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RxJavaActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTimerTv;
    private Toolbar toolbar;
    private Context mContext;

    private static final long MAX_TIME = 10;

    private Disposable mDisposable;
    private Observable<Long> mObservable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        mContext = this;
        mTimerTv = findViewById(R.id.tv_countTime);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_rxjava_user);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        initRxJava();
    }

    public void initRxJava() {
        mObservable = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .take(MAX_TIME / 1000 + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) {
                        return MAX_TIME - aLong * 1000;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

        subscribe();
    }

    /**
     * 每次订阅前先取消旧订阅，再创建新的 Observer 避免 Disposable 丢失
     */
    private void subscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(Long value) {
                mTimerTv.setText(TimeTools.getCountTimeByLong(value));
            }

            @Override
            public void onError(Throwable e) {
                Log.e("RxJavaActivity", "countdown failed", e);
            }

            @Override
            public void onComplete() {
                Log.e("RxJavaActivity", "onComplete");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_start) {
            if (mDisposable == null || mDisposable.isDisposed()) {
                subscribe();
            }
        } else if (viewId == R.id.btn_cancel) {
            if (mDisposable != null) {
                mDisposable.dispose();
            }
        }
    }
}
