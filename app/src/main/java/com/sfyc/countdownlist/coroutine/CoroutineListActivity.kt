package com.sfyc.countdownlist.coroutine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import com.sfyc.countdownlist.R
import com.sfyc.countdownlist.databinding.ActivityCoroutineListBinding
import com.sfyc.countdownlist.databinding.ListItemCoroutineBinding
import com.sfyc.countdownlist.utils.TimeTools
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 协程 + XML ViewBinding 实现多列表倒计时。
 * 使用单一协程统一刷新列表，避免每个条目单独启动定时器。
 */
class CoroutineListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoroutineListBinding

    private val items = mutableListOf<TimerData>()
    private lateinit var adapter: TimerAdapter
    private var tickJob: Job? = null
    private var nextId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoroutineListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViewById<Toolbar>(R.id.toolbar).title = "协程 多列表倒计时"

        initItems()
        adapter = TimerAdapter(items)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.btnAdd.setOnClickListener { addItem() }

        startGlobalTick()
    }

    private fun initItems() {
        val now = System.currentTimeMillis()
        val durations = longArrayOf(15_000, 30_000, 45_000, 60_000, 90_000, 120_000, 180_000, 300_000)
        val names = arrayOf("任务 A", "任务 B", "任务 C", "任务 D", "任务 E", "任务 F", "任务 G", "任务 H")
        durations.forEachIndexed { index, duration ->
            items.add(TimerData(nextId++, names[index], now + duration, duration))
        }
    }

    private fun addItem() {
        val now = System.currentTimeMillis()
        val duration = (10_000L..120_000L).random()
        val id = nextId++
        items.add(0, TimerData(id, "新任务 $id", now + duration, duration))
        adapter.notifyItemInserted(0)
        binding.recyclerView.scrollToPosition(0)
    }

    private fun startGlobalTick() {
        tickJob = lifecycleScope.launch {
            while (true) {
                delay(1000L)
                val now = System.currentTimeMillis()
                items.forEach { item ->
                    val remaining = (item.expirationTime - now).coerceAtLeast(0)
                    if (remaining != item.remaining) {
                        item.remaining = remaining
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tickJob?.cancel()
    }
}

data class TimerData(
    val id: Int,
    val name: String,
    val expirationTime: Long,
    val totalDuration: Long,
    var remaining: Long = totalDuration,
)

class TimerAdapter(
    private val items: List<TimerData>
) : RecyclerView.Adapter<TimerAdapter.VH>() {

    class VH(val binding: ListItemCoroutineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ListItemCoroutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val finished = item.remaining <= 0

        holder.binding.tvName.text = if (finished) "${item.name} (已完成)" else item.name
        holder.binding.tvTime.text = TimeTools.getCountTimeByLong(item.remaining)

        val progress = if (item.totalDuration > 0) {
            (item.remaining * 1000 / item.totalDuration).toInt()
        } else {
            0
        }
        holder.binding.progressBar.progress = progress

        val colorRes = when {
            finished -> R.color.timer_finished
            item.remaining < 10_000 -> R.color.timer_warning
            else -> R.color.timer_running
        }
        holder.binding.tvTime.setTextColor(
            ContextCompat.getColor(holder.itemView.context, colorRes)
        )
    }

    override fun getItemCount(): Int = items.size
}
