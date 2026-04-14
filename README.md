# CountDownList — Android 倒计时百科全书

[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-BOM%202026.03-green.svg)](https://developer.android.com/jetpack/compose)
[![Material3](https://img.shields.io/badge/Material%20Design-3-purple.svg)](https://m3.material.io)

一个全面演示 Android 倒计时 / 正计时实现方式的示例项目，涵盖 **8 种基础计时机制**、**20+ 个独立页面**，从传统 Java 到 Kotlin Coroutines、Flow、Jetpack Compose 的全方位技术方案对比。适合作为 Android 计时器领域的学习参考和技术选型依据。

---

## 快速开始

### 环境要求

- Android Studio Meerkat (2025.1+) 或更高版本
- JDK 17
- Android SDK，compileSdk 35

### 构建 & 运行

```bash
# 克隆项目
git clone https://github.com/user/CountDownList.git
cd CountDownList

# 构建 Debug 包
./gradlew :app:assembleDebug

# 输出位置
# app/build/outputs/apk/debug/app-debug.apk
```

直接用 Android Studio 打开项目，Gradle Sync 后即可运行到模拟器或真机。

---

## 功能全览

### 一、基础计时机制（8 种技术方案）

| 技术方案 | 核心 API | 特点 |
|---------|---------|------|
| Timer + TimerTask | `java.util.Timer` | 后台线程定时 + Handler 切主线程 |
| Handler.postDelayed | `android.os.Handler` | 主线程消息队列延迟递归 |
| CountDownTimer | `android.os.CountDownTimer` | Android SDK 封装，最简单的倒计时 |
| 自定义 CountDown | Handler + `SystemClock.elapsedRealtime` | 高精度、抗系统时间修改 |
| ValueAnimator | `android.animation.ValueAnimator` | 属性动画驱动，带进度条 |
| RxJava | `Observable.interval` | 响应式流，自动生命周期管理 |
| Chronometer | `android.widget.Chronometer` | 系统 View 组件倒计时模式 |
| AlarmManager | `android.app.AlarmManager` | 系统级闹钟 + 广播通知，跨进程可靠 |

### 二、列表倒计时（3 种）

- **ListView + BaseAdapter** — 传统列表方案
- **RecyclerView + SparseArray** — 每个 item 独立 CountDownTimer
- **RecyclerView + SwipeRefreshLayout** — 下拉刷新 + 倒计时

### 三、短信验证码（5 种）

Handler 工具类 · RxJava · RxBinding · Kotlin 协程 · 自定义 CountDownButton

### 四、协程 + XML（3 种）

- `lifecycleScope` 单个倒计时
- 协程统一刷新列表（单协程驱动多 item）
- 协程短信验证码

### 五、Flow + XML（2 种）

- **Flow 单个倒计时** — `TickerRepository.elapsedRealtimeFlow()` + `repeatOnLifecycle`
- **Flow 列表倒计时** — 单一 ticker 驱动 RecyclerView 可见区域刷新

### 六、MotionLayout 动画倒计时

- 三种状态间通过 MotionLayout Transition 平滑切换
- idle（数字居中）→ running（数字上移 + 圆环展开）→ finished（全屏闪烁）

### 七、Compose UI（5 Tab + 13 子页面）

**底部导航 5 Tab：**

| Tab | 功能 |
|-----|------|
| 倒计时 | CircularProgress 环形进度 + TimerDisplay + StatusChip + FilterChip 快捷时长 |
| 秒表 | 毫秒精度正计时 + 分段 Lap（最快/最慢自动高亮） |
| 列表 | LazyColumn 多任务倒计时 + FAB 添加 + 滑动删除 |
| 目标 | 日期目标倒计时（天数大卡片 + HH:MM:SS 实时更新） |
| 更多 | 13 个扩展功能入口 |

**"更多" 子页面：**

| 页面 | 说明 |
|------|------|
| 短信验证码 | Compose 实现的完整短信表单 |
| 番茄钟 | 工作/休息交替的多阶段倒计时 |
| 翻页时钟 | 经典 FlipClock 数字翻页动画 |
| 声明式倒计时 | LaunchedEffect + rememberSaveable 纯 Composable 驱动 |
| Deadline 模型 | 基于截止时间的抗重建倒计时 + SavedStateHandle |
| 后台 Service | ForegroundService + 通知栏实时更新 |
| 持久化 Demo | Room + Flow + WorkManager 端到端 |
| 循环倒计时 | 自动多轮重复 + 震动反馈 |
| 服务端校准 | 模拟 NTP 对时，对比校准前后差异 |
| 桌面小组件 | Glance App Widget 使用说明 |
| 通知栏倒计时 | 常驻通知 + 自定义 RemoteViews |
| Clean Arch | Domain → Data → Presentation 三层分离完整示例 |
| 技术对比 | 各方案精度/性能/适用场景横向对比矩阵 |

### 八、App Widget 桌面小组件

- 基于 **Jetpack Glance** 的桌面倒计时小组件
- 默认展示距下一个新年的倒计时（X 天 XX 时 XX 分 XX 秒）
- 长按桌面 → 小组件 → 找到 "倒计时" 即可添加

---

## 技术特点

### 架构与工程化

- **TimerEngine 抽象接口** — 计时引擎与显示层彻底分离，Widget / 通知栏 / Activity / Compose 共享同一引擎
- **TickerRepository** — 全局统一时间源 (`Flow<Long>`)，多页面订阅同一 ticker，避免重复创建定时器
- **Clean Architecture 示例** — Domain (UseCase + Repository 接口) → Data (Room 实现) → Presentation (ViewModel + Compose)
- **Deadline 状态模型** — 只持久化截止时间，UI 每次用 `SystemClock.elapsedRealtime()` 反推剩余，抗配置变更、抗进程重建

### 持久化与后台

- **Room** — 倒计时任务持久化存储，Flow 观察实时变更
- **DataStore** — 偏好设置替代 SharedPreferences
- **WorkManager** — 倒计时到期后可靠触发通知 / 状态变更
- **ForegroundService** — 长时后台倒计时 + 通知栏实时刷新

### UI 设计

- 全局 **Material Design 3** (Material You) 设计规范
- 统一 Light / Dark 配色方案（Blue Violet 主色系）
- 语义化 Timer 颜色（运行中 / 暂停 / 警告 / 完成 / 秒表 五种状态）
- Monospace 等宽字体用于所有计时器显示
- Compose 自定义组件：`CircularTimerProgress`、`TimerDisplay`、`StatusChip`、`FlipDigit`

### 反馈与体验

- **FeedbackUtil** — SoundPool 音效 + Vibrator 震动的统一工具类
- 最后 3 秒 tick 短促反馈，结束时长震动
- 适配 Android O+ VibrationEffect 和 Android S+ VibratorManager

---

## 构建环境

| 组件 | 版本 |
|------|------|
| Android Gradle Plugin | 9.1.0 |
| Kotlin | 2.3.10 |
| Gradle Wrapper | 9.3.1 |
| JDK | 17 |
| compileSdk / targetSdk | 35 |
| minSdk | 24 |
| Compose BOM | 2026.03.00 |
| Material | 1.12.0 |
| Lifecycle | 2.9.0 |
| Room | 2.7.1 |
| WorkManager | 2.10.1 |
| DataStore | 1.1.7 |
| Glance | 1.1.1 |
| RxJava | 3.1.10 |

---

## 项目结构

```
app/src/main/java/com/sfyc/countdownlist/
├── compose/                        # Jetpack Compose UI
│   ├── ComposeMainActivity.kt      # 5 Tab + 13 子页面入口
│   ├── component/                  # 基础组件 (TimerDisplay, CircularTimerProgress, StatusChip, FlipDigit...)
│   ├── screen/                     # 17 个页面
│   ├── viewmodel/                  # ViewModels (Deadline, Repeat, ServerSync, Persistent...)
│   └── theme/                      # Material3 Theme + 语义颜色
├── clean/                          # Clean Architecture 示例
│   ├── domain/                     # UseCase + Repository 接口 + Model
│   ├── data/                       # Room 实现
│   └── presentation/               # ViewModel + Compose Screen
├── engine/                         # TimerEngine 抽象 + DeadlineTimerEngine + TickerRepository
├── service/                        # ForegroundService 后台计时
├── worker/                         # WorkManager Worker
├── data/                           # Room Entity / DAO / Database + DataStore
├── flow/                           # Flow + XML 实现
├── coroutine/                      # 协程 + XML 实现
├── listview/                       # RecyclerView / ListView 列表
├── sms/                            # 短信验证码各方案
├── receiver/                       # BroadcastReceiver
├── utils/                          # 工具类 (FeedbackUtil, TimeTools, PrefUtils...)
├── widget/                         # 自定义 View + Glance App Widget
└── *.java / *.kt                   # 基础倒计时 Activity + MotionTimerActivity
```

---

## License

```
Copyright 2026 sfyc

Licensed under the Apache License, Version 2.0
```
