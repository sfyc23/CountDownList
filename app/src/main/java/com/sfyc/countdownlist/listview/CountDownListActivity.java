package com.sfyc.countdownlist.listview;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sfyc.countdownlist.R;
import com.sfyc.countdownlist.entity.TimerItem;
import com.sfyc.countdownlist.utils.TimeTools;
import com.sfyc.countdownlist.utils.TimerItemUtil;

import java.util.List;

public class CountDownListActivity extends AppCompatActivity {

    private Context mContext;
    private Toolbar mToolbar;
    private ListView mListView;

    private MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        mContext = this;
        mToolbar = findViewById(R.id.toolbar);
        mListView = findViewById(R.id.list_view);
        mToolbar.setTitle(R.string.title_list_view_countdown);

        mAdapter = new MyAdapter(mContext, TimerItemUtil.getTimerItemList());
        mListView.setAdapter(mAdapter);
    }

    public static class MyAdapter extends BaseAdapter {
        private final List<TimerItem> mDatas;
        private final Context mContext;
        private final SparseArray<CountDownTimer> countDownCounters;

        public MyAdapter(Context mContext, List<TimerItem> mDatas) {
            this.mContext = mContext;
            this.mDatas = mDatas;
            this.countDownCounters = new SparseArray<>();
        }

        public void cancelAllTimers() {
            for (int i = 0, length = countDownCounters.size(); i < length; i++) {
                CountDownTimer countDownTimer = countDownCounters.get(countDownCounters.keyAt(i));
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
            }
        }

        @Override
        public int getCount() {
            return mDatas == null ? 0 : mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas == null ? null : mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_common, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.statusTv = convertView.findViewById(R.id.tv_status);
                viewHolder.timeTv = convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final TimerItem data = mDatas.get(position);
            viewHolder.statusTv.setText(data.name);

            CountDownTimer countDownTimer = countDownCounters.get(position);
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            long timer = data.expirationTime - System.currentTimeMillis();
            if (timer > 0) {
                countDownTimer = new CountDownTimer(timer, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        viewHolder.timeTv.setText(TimeTools.getCountTimeByLong(millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        viewHolder.timeTv.setText("00:00:00");
                        viewHolder.statusTv.setText(data.name + ":finished");
                    }
                }.start();
                countDownCounters.put(position, countDownTimer);
            } else {
                viewHolder.timeTv.setText("00:00:00");
                viewHolder.statusTv.setText(data.name + ":finished");
            }
            return convertView;
        }

        public static class ViewHolder {
            public TextView statusTv;
            public TextView timeTv;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cancelAllTimers();
        }
    }
}
