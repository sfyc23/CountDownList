package com.sfyc.countdownlist.flow

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sfyc.countdownlist.R
import com.sfyc.countdownlist.engine.TickerRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.launch

/**
 * Flow + XML 列表倒计时 Demo。
 * 全局统一 ticker 驱动，Adapter 只负责 bind，
 * 不为每个 item 创建独立计时器。
 */
class FlowListActivity : AppCompatActivity() {

    private data class TimerItem(
        val name: String,
        val totalMs: Long,
        val deadlineRealtime: Long,
    )

    private val items = mutableListOf<TimerItem>()
    private lateinit var adapter: TimerAdapter
    private var nextId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow_list)

        findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = "Flow 列表倒计时"
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { finish() }
        }

        adapter = TimerAdapter()
        val rv = findViewById<RecyclerView>(R.id.recycler_view)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        repeat(5) { addItem() }

        findViewById<MaterialButton>(R.id.btn_add).setOnClickListener { addItem() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                TickerRepository.tickerFlow(intervalMs = 1000L).collect {
                    adapter.notifyItemRangeChanged(0, items.size, "tick")
                }
            }
        }
    }

    private fun addItem() {
        val seconds = listOf(30, 60, 90, 120, 180).random().toLong()
        items.add(
            TimerItem(
                name = "任务 ${nextId++}",
                totalMs = seconds * 1000,
                deadlineRealtime = SystemClock.elapsedRealtime() + seconds * 1000,
            )
        )
        adapter.notifyItemInserted(items.size - 1)
    }

    private inner class TimerAdapter : RecyclerView.Adapter<TimerAdapter.VH>() {

        inner class VH(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_flow, parent, false)
        ) {
            val tvName: TextView = itemView.findViewById(R.id.tv_name)
            val tvTime: TextView = itemView.findViewById(R.id.tv_time)
            val progress: LinearProgressIndicator = itemView.findViewById(R.id.progress)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(parent)
        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            holder.tvName.text = item.name
            val remaining = (item.deadlineRealtime - SystemClock.elapsedRealtime()).coerceAtLeast(0)
            val sec = (remaining / 1000).toInt()
            holder.tvTime.text = String.format("%02d:%02d", sec / 60, sec % 60)
            holder.progress.max = item.totalMs.toInt()
            holder.progress.progress = remaining.toInt()

            val colorRes = when {
                remaining <= 0 -> R.color.timer_finished
                remaining <= 10_000 -> R.color.timer_warning
                else -> R.color.timer_running
            }
            holder.tvTime.setTextColor(ContextCompat.getColor(holder.itemView.context, colorRes))
        }
    }
}
