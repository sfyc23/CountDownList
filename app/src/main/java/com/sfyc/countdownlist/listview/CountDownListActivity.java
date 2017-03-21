package com.sfyc.countdownlist.listview;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sfyc.countdownlist.R;
import com.sfyc.countdownlist.entity.TimerItem;
import com.sfyc.countdownlist.utils.TimerItemUtil;
import com.sfyc.countdownlist.utils.TimeTools;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sfyc.countdownlist.R.id.toolbar;

/**
 * Author :leilei on 2017/3/21 1411.
 */

public class CountDownListActivity extends AppCompatActivity {

    private Context mContext;

    @BindView(toolbar)
    Toolbar mToolbar;

    @BindView(R.id.list_view)
    ListView mListView;

    MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        ButterKnife.bind(this);
        mContext = this;
        mToolbar.setTitle(R.string.title_list_view_countdown);

        mAdapter = new MyAdapter(mContext,TimerItemUtil.getTimerItemList());
        mListView.setAdapter(mAdapter);
    }

    public static class MyAdapter extends BaseAdapter {
        private List<TimerItem> mDatas;
        private Context mContext;
        //用于退出activity,避免countdown，造成资源浪费。
        private SparseArray<CountDownTimer> countDownCounters;

        public MyAdapter(Context mContext, List<TimerItem> mDatas) {
            this.mContext = mContext;
            this.mDatas = mDatas;
            this.countDownCounters = new SparseArray<>();
        }

        /**
         * 清空资源
         */
        public void cancelAllTimers() {
            if (countDownCounters == null) {
                return;
            }
            Log.e("TAG",  "size :  " + countDownCounters.size());
            for (int i = 0, length = countDownCounters.size(); i < length; i++) {
                CountDownTimer cdt = countDownCounters.get(countDownCounters.keyAt(i));
                if (cdt != null) {
                    cdt.cancel();
                }
            }
        }

        @Override
        public int getCount() {
            if (mDatas != null && !mDatas.isEmpty()) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null && !mDatas.isEmpty()) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_common, parent, false);;
                viewHolder = new ViewHolder();
                viewHolder.statusTv = (TextView) convertView.findViewById(R.id.tv_status);
                viewHolder.timeTv = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final TimerItem data = mDatas.get(position);
            viewHolder.statusTv.setText(data.name);

            CountDownTimer countDownTimer = countDownCounters.get(viewHolder.timeTv.hashCode());
            //将前一个缓存清除
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            long timer = data.expirationTime;
            timer = timer - System.currentTimeMillis();
            if (timer > 0) {
                countDownTimer = new CountDownTimer(timer, 1000) {
                    public void onTick(long millisUntilFinished) {
                        viewHolder.timeTv.setText(TimeTools.getCountTimeByLong(millisUntilFinished));
                        Log.e("TAG", data.name + " :  " + millisUntilFinished);
                    }
                    public void onFinish() {
                        viewHolder.timeTv.setText("00:00:00");
                        viewHolder.statusTv.setText(data.name + ":结束");
                    }
                }.start();
                countDownCounters.put(viewHolder.timeTv.hashCode(), countDownTimer);
            } else {
                viewHolder.timeTv.setText("00:00:00");
                viewHolder.statusTv.setText(data.name + ":结束");
            }
            return convertView;
        }

        public class ViewHolder {
            public TextView statusTv;
            public TextView timeTv;
        }
    }




}
