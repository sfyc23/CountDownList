package com.sfyc.countdownlist.listview;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sfyc.countdownlist.R;
import com.sfyc.countdownlist.entity.TimerItem;
import com.sfyc.countdownlist.utils.TimeTools;
import com.sfyc.countdownlist.utils.TimerItemUtil;

import java.util.List;

public class CountDownRecyclerViewActivity2 extends AppCompatActivity {

    private Context mContext;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mContext = this;
        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mToolbar.setTitle(R.string.title_recyclerView_countdown);
        List<TimerItem> timerItems = TimerItemUtil.getTimerItemList();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyAdapter(timerItems);
        mRecyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addItem(new TimerItem("new", System.currentTimeMillis() + 11 * 1000));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 200);
            }
        });
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private final List<TimerItem> mDatas;
        private final SparseArray<CountDownTimer> countDownMap;

        public MyAdapter(List<TimerItem> datas) {
            mDatas = datas;
            countDownMap = new SparseArray<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_common, parent, false);
            return new ViewHolder(view);
        }

        public void cancelAllTimers() {
            for (int i = 0, length = countDownMap.size(); i < length; i++) {
                CountDownTimer countDownTimer = countDownMap.get(countDownMap.keyAt(i));
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
            }
        }

        public void addItem(TimerItem item) {
            mDatas.add(0, item);
            notifyItemInserted(0);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final TimerItem data = mDatas.get(position);
            holder.statusTv.setText(data.name);

            CountDownTimer oldTimer = countDownMap.get(position);
            if (oldTimer != null) {
                oldTimer.cancel();
            }
            if (holder.countDownTimer != null) {
                holder.countDownTimer.cancel();
            }

            long time = data.expirationTime - System.currentTimeMillis();
            if (time > 0) {
                holder.countDownTimer = new CountDownTimer(time, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        holder.timeTv.setText(TimeTools.getCountTimeByLong(millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        holder.timeTv.setText("00:00:00");
                        holder.statusTv.setText(data.name + ":finished");
                    }
                }.start();
                countDownMap.put(position, holder.countDownTimer);
            } else {
                holder.timeTv.setText("00:00:00");
                holder.statusTv.setText(data.name + ":finished");
            }
        }

        @Override
        public int getItemCount() {
            return mDatas == null ? 0 : mDatas.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView statusTv;
            public final TextView timeTv;
            public CountDownTimer countDownTimer;

            public ViewHolder(View itemView) {
                super(itemView);
                statusTv = itemView.findViewById(R.id.tv_status);
                timeTv = itemView.findViewById(R.id.tv_time);
            }
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
