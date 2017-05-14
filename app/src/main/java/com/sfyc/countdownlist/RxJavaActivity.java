package com.sfyc.countdownlist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sfyc.countdownlist.utils.TimeTools;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Author :leilei on 2016/11/11 2300.
 * RxJava 动画 实现 倒计时
 */
public class RxJavaActivity extends AppCompatActivity {

    @BindView(R.id.tv_countTime)
    TextView mTimerTv;

    private Context mContext;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private static final long MAX_TIME = 10;

    /**
     * 取消
     */
    private Disposable mDisposable;
    private Observable mObservable;
    private Observer mObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        ButterKnife.bind(this);
        mContext = this;
        toolbar.setTitle(R.string.title_rxjava_user);
        initRxJava();

    }



    public void initRxJava() {
        /**
         * RxJava 方式实现
         */
        mObservable = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)//它在指定延迟之后先发射一个零值，然后再按照指定的时间间隔发射递增的数字,设置0延迟，每隔1000毫秒发送一条数据
                .take(MAX_TIME / 1000 + 1)//设置总共发送的次数
                .map(new Function<Long, Long>() {//long 值是从0到最大，倒计时需要将值倒置
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return MAX_TIME - aLong * 1000;
                    }
                })
                .subscribeOn(Schedulers.io())//倒计时放在io线程中
                .observeOn(AndroidSchedulers.mainThread());//显示放在主线程。

        mObserver = new Observer<Long>() {
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

            }

            @Override
            public void onComplete() {
                Log.e("TAG","onComplete");

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

    @OnClick({R.id.btn_start, R.id.btn_cancel, R.id.btn_pause, R.id.btn_resume})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if (mDisposable == null || mDisposable.isDisposed()) {
                    mObservable.subscribe(mObserver);
                }
                break;
            case R.id.btn_cancel:
                if (mDisposable != null) {
                    mDisposable.dispose();
                }
                break;
            case R.id.btn_pause:

                break;
            case R.id.btn_resume:

                break;
            default:
                break;
        }
    }

}
