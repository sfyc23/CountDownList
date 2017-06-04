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
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author :leilei on 2017/5/1 23:43
 * 用RxbindingA 实现短信倒计时
 */
public class SmsRxbindingActivity extends AppCompatActivity {
    private static final String TAG = "SmsRxbindingActivity";

    private Context mContext;


    private Toolbar mToolbar;
    private TextView mBtnSendMsm;
    private Button mBtnClean;

    //最大倒计时长
    private static final long MAX_COUNT_TIME = 10;

    private Observable<Long> mObservableCountTime;
    private Consumer<Long> mConsumerCountTime;

    //用于主动取消订阅倒计时，或者退出当前页面。
    private Disposable mDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        mContext = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_sms_rxbinding);

        mBtnSendMsm = (TextView) findViewById(R.id.btn_send_sms);
        mBtnClean = (Button) findViewById(R.id.btn_sms_submit);


        //重置验证码按钮。
        RxView.clicks(mBtnClean).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (mDisposable != null && !mDisposable.isDisposed()) {
                    //停止倒计时
                    mDisposable.dispose();
                    //重新订阅
                    mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
                    //按钮可点击
                    RxView.enabled(mBtnSendMsm).accept(true);
                    RxTextView.text(mBtnSendMsm).accept("发送验证码");
                }
            }
        });



        mObservableCountTime = RxView.clicks(mBtnSendMsm)
                //防止重复点击
                .throttleFirst(MAX_COUNT_TIME, TimeUnit.SECONDS)
                //将点击事件转换成倒计时事件
                .flatMap(new Function<Object, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(Object o) throws Exception {
                        //更新发送按钮的状态并初始化显现倒计时文字
                        RxView.enabled(mBtnSendMsm).accept(false);
                        RxTextView.text(mBtnSendMsm).accept("剩余 " + MAX_COUNT_TIME + " 秒");
                        //在实际操作中可以在此发送获取网络的请求,,续1s
                        return Observable.interval(1, TimeUnit.SECONDS, Schedulers.io()).take(MAX_COUNT_TIME);
                    }
                })
                //将递增数字替换成递减的倒计时数字
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return MAX_COUNT_TIME - (aLong + 1);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        mConsumerCountTime = new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                //当倒计时为 0 时，还原btn按钮
                if (aLong == 0) {
                    RxView.enabled(mBtnSendMsm).accept(true);
                    RxTextView.text(mBtnSendMsm).accept("发送验证码");
                } else {
                    RxTextView.text(mBtnSendMsm).accept("剩余 " + aLong + " 秒");
                }
            }
        };

        //订阅
        mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }


}
