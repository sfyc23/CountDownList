package com.sfyc.countdownlist.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.sfyc.countdownlist.compose.screen.DateTargetScreen
import com.sfyc.countdownlist.compose.screen.DeadlineTimerScreen
import com.sfyc.countdownlist.compose.screen.DeclarativeTimerScreen
import com.sfyc.countdownlist.compose.screen.FlipClockScreen
import com.sfyc.countdownlist.compose.screen.ForegroundServiceScreen
import com.sfyc.countdownlist.compose.screen.ListCountDownScreen
import com.sfyc.countdownlist.compose.screen.PersistentTimerScreen
import com.sfyc.countdownlist.compose.screen.MoreScreen
import com.sfyc.countdownlist.compose.screen.PomodoroScreen
import com.sfyc.countdownlist.compose.screen.NotificationTimerScreen
import com.sfyc.countdownlist.compose.screen.RepeatTimerScreen
import com.sfyc.countdownlist.compose.screen.ServerSyncScreen
import com.sfyc.countdownlist.compose.screen.WidgetInfoScreen
import com.sfyc.countdownlist.clean.presentation.CleanArchScreen
import com.sfyc.countdownlist.compose.screen.SingleCountDownScreen
import com.sfyc.countdownlist.compose.screen.SmsCountDownScreen
import com.sfyc.countdownlist.compose.screen.StopwatchScreen
import com.sfyc.countdownlist.compose.screen.TechComparisonScreen
import com.sfyc.countdownlist.compose.theme.CountDownTheme

private const val SUB_NONE = ""
private const val SUB_SMS = "sms"
private const val SUB_POMODORO = "pomodoro"
private const val SUB_FLIP = "flip"
private const val SUB_TECH = "tech"
private const val SUB_DECLARATIVE = "declarative"
private const val SUB_DEADLINE = "deadline"
private const val SUB_SERVICE = "service"
private const val SUB_PERSISTENT = "persistent"
private const val SUB_REPEAT = "repeat"
private const val SUB_SERVERSYNC = "serversync"
private const val SUB_WIDGET_INFO = "widget_info"
private const val SUB_NOTIFICATION = "notification"
private const val SUB_CLEAN = "clean"

class ComposeMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CountDownTheme {
                ComposeMainScreen()
            }
        }
    }
}

private enum class Tab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    COUNTDOWN("倒计时", Icons.Filled.Timer, Icons.Outlined.Timer),
    STOPWATCH("秒表", Icons.Filled.Timer, Icons.Outlined.Timer),
    LIST("列表", Icons.Filled.List, Icons.Outlined.List),
    TARGET("目标", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    MORE("更多", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComposeMainScreen() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var subPage by rememberSaveable { mutableStateOf(SUB_NONE) }
    val tabs = Tab.entries
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val titleText = when (subPage) {
        SUB_SMS -> "短信验证码"
        SUB_POMODORO -> "番茄钟"
        SUB_FLIP -> "翻页时钟"
        SUB_TECH -> "技术方案对比"
        SUB_DECLARATIVE -> "声明式倒计时"
        SUB_DEADLINE -> "Deadline 模型"
        SUB_SERVICE -> "后台 Service"
        SUB_PERSISTENT -> "持久化 Demo"
        SUB_REPEAT -> "循环倒计时"
        SUB_SERVERSYNC -> "服务端校准"
        SUB_WIDGET_INFO -> "桌面小组件"
        SUB_NOTIFICATION -> "通知栏倒计时"
        SUB_CLEAN -> "Clean Architecture"
        else -> tabs[selectedTab].label
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(titleText) },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            subPage = SUB_NONE
                        },
                        icon = {
                            Icon(
                                if (selectedTab == index) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label,
                            )
                        },
                        label = { Text(tab.label) },
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (subPage) {
                SUB_SMS -> SmsCountDownScreen()
                SUB_POMODORO -> PomodoroScreen()
                SUB_FLIP -> FlipClockScreen()
                SUB_TECH -> TechComparisonScreen()
                SUB_DECLARATIVE -> DeclarativeTimerScreen()
                SUB_DEADLINE -> DeadlineTimerScreen()
                SUB_SERVICE -> ForegroundServiceScreen()
                SUB_PERSISTENT -> PersistentTimerScreen()
                SUB_REPEAT -> RepeatTimerScreen()
                SUB_SERVERSYNC -> ServerSyncScreen()
                SUB_WIDGET_INFO -> WidgetInfoScreen()
                SUB_NOTIFICATION -> NotificationTimerScreen()
                SUB_CLEAN -> CleanArchScreen()
                else -> {
                    Crossfade(targetState = tabs[selectedTab], label = "tab_crossfade") { tab ->
                        when (tab) {
                            Tab.COUNTDOWN -> SingleCountDownScreen()
                            Tab.STOPWATCH -> StopwatchScreen()
                            Tab.LIST -> ListCountDownScreen()
                            Tab.TARGET -> DateTargetScreen()
                            Tab.MORE -> MoreScreen(
                                onNavigate = { key -> subPage = key },
                            )
                        }
                    }
                }
            }
        }
    }
}
