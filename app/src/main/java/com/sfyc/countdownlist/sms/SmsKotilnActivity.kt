package com.sfyc.countdownlist.sms

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.sfyc.countdownlist.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SmsKotilnActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var sendSmsButton: Button
    private lateinit var submitButton: Button

    private var observable: Observable<Long>? = null
    private var disposable: Disposable? = null
    private var consumer: Consumer<Long>? = null

    private val maxCountTime = 10L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)

        toolbar = findViewById(R.id.toolbar)
        sendSmsButton = findViewById(R.id.btn_send_sms)
        submitButton = findViewById(R.id.btn_sms_submit)

        toolbar.setTitle(R.string.title_sms_kotiln)

        sendSmsButton.setOnClickListener {
            if (observable == null) {
                initCountDown()
            }
            consumer?.let { currentConsumer ->
                disposable = observable?.subscribe(currentConsumer)
            }
        }
        submitButton.setOnClickListener {
            disposable?.dispose()
            sendSmsButton.isEnabled = true
            sendSmsButton.setText(R.string.sms_send_code)
        }
    }

    private fun initCountDown() {
        observable = Observable.interval(1, TimeUnit.SECONDS)
            .take(maxCountTime + 1)
            .map { maxCountTime - it - 1 }
            .subscribeOn(Schedulers.computation())
            .doOnSubscribe {
                sendSmsButton.isEnabled = false
                sendSmsButton.text = "${maxCountTime}s"
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                sendSmsButton.isEnabled = true
                sendSmsButton.setText(R.string.sms_send_code)
            }
        consumer = Consumer<Long> { remaining ->
            sendSmsButton.text = "${remaining}s"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
