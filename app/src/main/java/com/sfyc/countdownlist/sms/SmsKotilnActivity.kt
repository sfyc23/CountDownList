package com.sfyc.countdownlist.sms

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sfyc.countdownlist.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sms.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.util.concurrent.TimeUnit

class SmsKotilnActivity : AppCompatActivity() {

    internal var mObservable: Observable<Long>? = null
    private var mDisposable: Disposable? = null
    private var mConsumer: Consumer<Long>? = null

    private val MAX_COUNT_TIME: Long = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)

        toolbar.setTitle(R.string.title_sms_kotiln)

        btn_send_sms.setOnClickListener {
            if (mObservable === null) {
                initCountDown()
            }
            mDisposable = mObservable?.subscribe(mConsumer)
        }
        btn_sms_submit.setOnClickListener {
            mDisposable?.dispose()
            btn_send_sms.isEnabled = true
            btn_send_sms.setText(R.string.sms_send_code)
        }

    }

    private fun initCountDown() {
        mObservable = Observable.interval(1, TimeUnit.SECONDS)
                //续 1s.
                .take(MAX_COUNT_TIME + 1)
                .map {
                    MAX_COUNT_TIME - it - 1
                }
                .subscribeOn(Schedulers.computation())
                .doOnSubscribe {
                    btn_send_sms.isEnabled = false;
                    btn_send_sms.text = "剩余 ${MAX_COUNT_TIME} s"
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    btn_send_sms.isEnabled = true;
                    btn_send_sms.setText(R.string.sms_send_code)
                }
        mConsumer = Consumer<Long> {
            btn_send_sms.text = "剩余 ${it} s"
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable?.dispose()
    }
}

