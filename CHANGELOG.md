# 修改日志 (CHANGELOG)

## 2026-03-31 — MotionLayout + 通知栏常驻 + Clean Architecture + 首页扩展

### 新增功能

#### 1. MotionLayout 动画倒计时 (MotionTimerActivity)
- **文件**: `MotionTimerActivity.kt`, `res/layout/activity_motion_timer.xml`, `res/xml/motion_scene_timer.xml`
- **功能**: 三种状态间通过 MotionLayout transition 动画切换
  - idle: 数字居中，进度环隐藏
  - running: 数字移到顶部，圆环进度展开
  - finished: 数字放大，全屏闪烁覆盖层
- **技术**: ConstraintLayout MotionScene、ConstraintSet、Transition

#### 2. 通知栏常驻倒计时 (NotificationTimerScreen)
- **文件**: `compose/screen/NotificationTimerScreen.kt`, `res/layout/notification_timer.xml`
- **功能**: 通过 ForegroundService 在通知栏实时显示倒计时进度
- **特性**: setOngoing(true) 常驻通知、自定义 RemoteViews 布局、每秒更新

#### 3. MVVM Clean Architecture 完整示例
- **Domain 层**: `clean/domain/TimerTaskModel.kt`, `TimerTaskRepository.kt`, `TimerUseCases.kt`
- **Data 层**: `clean/data/RoomTimerTaskRepository.kt` (Room Entity ↔ Domain Model 映射)
- **Presentation 层**: `clean/presentation/CleanTimerViewModel.kt`, `CleanArchScreen.kt`
- **分层原则**: ViewModel 只依赖 UseCase，不直接操作 Room；Domain 层纯 Kotlin

### 首页更新
- **文件**: `res/layout/activity_main.xml`, `MainActivity.java`
- **变更**: XML 首页新增 "动画方案" 分组卡片，包含 MotionLayout 动画倒计时入口

### 导航接入
- **文件**: `compose/screen/MoreScreen.kt`, `compose/ComposeMainActivity.kt`
- **变更**: "更多" 页新增 "通知栏倒计时"、"Clean Arch" 两个入口

### Manifest 更新
- **文件**: `AndroidManifest.xml`
- **新增**: `MotionTimerActivity` 注册

---

## 2026-03-31 — 循环倒计时 + 服务端校准 + 音效震动 + App Widget

### 新增功能

#### 1. 循环/重复倒计时 (RepeatTimerScreen)
- **文件**: `compose/viewmodel/RepeatTimerViewModel.kt`, `compose/screen/RepeatTimerScreen.kt`
- **功能**: 支持选择时长 (10s/30s/1m) 和轮次 (3/5/10)，倒计时结束后自动进入下一轮
- **反馈**: 每轮结束触发震动，使用 `VibrationEffect` 提供触觉反馈
- **UI**: FilterChip 选择配置、CircularTimerProgress 环形进度、轮次状态显示

#### 2. 服务端时间校准倒计时 (ServerSyncScreen)
- **文件**: `compose/viewmodel/ServerSyncViewModel.kt`, `compose/screen/ServerSyncScreen.kt`
- **功能**: 模拟客户端与服务端的时钟偏移（-3s ~ +3s 随机偏移）
- **对比展示**: 同时显示「无校准」和「有校准」两个倒计时，直观展示偏差
- **适用场景**: 秒杀、活动开始、验证码过期等对绝对时间敏感的场景

#### 3. 音效/震动反馈工具 (FeedbackUtil)
- **文件**: `utils/FeedbackUtil.kt`
- **功能**: 封装 `SoundPool` + `Vibrator` 的统一工具类
- **接口**: `tick()` (最后几秒短促反馈)、`finish()` (结束长震动+音效)、`vibrate(ms)`
- **兼容性**: 适配 Android O+ `VibrationEffect` 和 Android S+ `VibratorManager`

#### 4. App Widget 桌面小组件 (TimerGlanceWidget)
- **文件**: `widget/TimerGlanceWidget.kt`, `res/xml/timer_widget_info.xml`, `res/layout/widget_timer_loading.xml`
- **功能**: 基于 Jetpack Glance 的桌面倒计时小组件，默认展示距下一个新年的倒计时
- **显示**: "X 天 XX 时 XX 分 XX 秒" 格式，等宽字体
- **刷新**: updatePeriodMillis 30 分钟，Receiver 触发时立即更新

#### 5. Widget 说明页 (WidgetInfoScreen)
- **文件**: `compose/screen/WidgetInfoScreen.kt`
- **功能**: 解释如何添加桌面小组件，以及 Widget 与页面内倒计时的技术差异

### 导航接入
- **文件**: `compose/screen/MoreScreen.kt`, `compose/ComposeMainActivity.kt`
- **变更**: "更多" 页新增 "循环倒计时"、"服务端校准"、"桌面小组件" 三个入口

### Gradle 依赖
- **文件**: `app/build.gradle`
- **新增**: `androidx.glance:glance-appwidget:1.1.1`, `androidx.glance:glance-material3:1.1.1`

### Manifest 更新
- **文件**: `AndroidManifest.xml`
- **新增**: `TimerWidgetReceiver` (AppWidgetProvider) 注册

---

## 2026-03-31 — Room + WorkManager + DataStore + TimerEngine + 端到端 Demo

### Gradle 依赖升级

- 项目级 `build.gradle`：新增 KSP 插件 `com.google.devtools.ksp:2.3.10-1.0.30`
- App 级 `build.gradle`：新增 Room 2.7.1、WorkManager 2.10.1、DataStore Preferences 1.1.7

### TimerEngine 抽象接口 — 引擎与显示层分离

- 新建 `engine/TimerEngine.kt`：统一接口 `start/pause/resume/cancel` + `remainingFlow` + `isRunningFlow`
- 新建 `engine/DeadlineTimerEngine.kt`：基于 elapsedRealtime 的实现，配合 TickerRepository 驱动

### DataStore — 替代 SharedPreferences

- 新建 `data/TimerPreferences.kt`：
  - `defaultDurationFlow` / `pomodoroWorkMinutesFlow` / `pomodoroRestMinutesFlow` / `pomodoroRoundsFlow`
  - 协程异步写入、Flow 响应式读取、类型安全

### Room 持久化倒计时任务

- 新建 `data/TimerEntity.kt`：`@Entity(tableName = "timer_tasks")`，字段：id / name / deadlineMs / durationMs / createdAt / status
- 新建 `data/TimerDao.kt`：`observeAll()` / `observeByStatus()` / `insert()` / `update()` / `delete()` / `updateStatus()`，全部返回 Flow 或 suspend
- 新建 `data/TimerDatabase.kt`：单例 `Room.databaseBuilder`

### WorkManager 延迟任务

- 新建 `worker/TimerExpiredWorker.kt`：
  - `CoroutineWorker`，倒计时到期后更新 Room 状态 + 发送高优先级通知
  - `schedule()` / `cancel()` 静态方法，支持按 timerId 调度和取消
  - 与 AlarmManager 互补：WorkManager 保证执行，即使 App 被杀

### 端到端 Compose Demo — PersistentTimerScreen

- 新建 `compose/viewmodel/PersistentTimerViewModel.kt`：
  - `AndroidViewModel`，持有 `TimerDao`，暴露 `allTimers: StateFlow<List<TimerEntity>>`
  - `addTimer()` → Room 插入 + WorkManager 调度
  - `cancelTimer()` / `deleteTimer()` → Room 状态更新 + WorkManager 取消
- 新建 `compose/screen/PersistentTimerScreen.kt`：
  - FAB 添加随机任务，LazyColumn 展示任务列表
  - 每个任务卡片显示名称 / 状态 / 剩余时间（语义化颜色）
  - 运行中可取消，已完成/已取消可删除

### MoreScreen + ComposeMainActivity 导航扩展

- `MoreScreen.kt`：新增"持久化 Demo"入口
- `ComposeMainActivity.kt`：新增 `SUB_PERSISTENT` 路由

### 新增/修改文件列表

| 文件 | 操作 |
|------|------|
| `build.gradle` (项目级) | 修改 — 新增 KSP 插件 |
| `app/build.gradle` | 修改 — 新增 Room/WorkManager/DataStore/KSP |
| `engine/TimerEngine.kt` | 新增 |
| `engine/DeadlineTimerEngine.kt` | 新增 |
| `data/TimerPreferences.kt` | 新增 |
| `data/TimerEntity.kt` | 新增 |
| `data/TimerDao.kt` | 新增 |
| `data/TimerDatabase.kt` | 新增 |
| `worker/TimerExpiredWorker.kt` | 新增 |
| `compose/viewmodel/PersistentTimerViewModel.kt` | 新增 |
| `compose/screen/PersistentTimerScreen.kt` | 新增 |
| `compose/screen/MoreScreen.kt` | 修改 |
| `compose/ComposeMainActivity.kt` | 修改 |

---

## 2026-03-31 — 统一时钟中心 + Flow/XML + 声明式 + Deadline + Foreground Service

### TickerRepository — 统一时钟中心

- 新建 `engine/TickerRepository.kt`：全局统一时钟 Flow
- `tickerFlow()`: 每秒递增 tick 的冷流，多个 collector 独立计数
- `elapsedRealtimeFlow()`: 基于 `SystemClock.elapsedRealtime()` 的时间源流，适合 Deadline 模型

### Flow + XML 方案

- 新建 `flow/FlowSingleActivity.kt` + `activity_flow_single.xml`：Flow 驱动单个倒计时
  - 使用 `repeatOnLifecycle(STARTED)` + `TickerRepository.elapsedRealtimeFlow()` 驱动 UI
  - Deadline 模型：只持有 deadline，UI 用 deadline - now 计算剩余
  - `LinearProgressIndicator` + 语义化状态颜色
- 新建 `flow/FlowListActivity.kt` + `activity_flow_list.xml` + `list_item_flow.xml`：Flow 驱动列表倒计时
  - 全局统一 ticker 驱动，Adapter 只负责 bind
  - `notifyItemRangeChanged(payload="tick")` 只刷新可见区域的时间文字
  - `MaterialCardView` 列表项 + `LinearProgressIndicator`

### Compose 纯声明式倒计时（DeclarativeTimerScreen）

- 新建 `compose/screen/DeclarativeTimerScreen.kt`
- 无 ViewModel，完全由 `LaunchedEffect(isRunning, remaining)` + `rememberSaveable` 驱动
- 配置变更（屏幕旋转）后状态自动恢复
- 附带实现原理说明卡片

### Deadline 模型（DeadlineTimerViewModel + DeadlineTimerScreen）

- 新建 `compose/viewmodel/DeadlineTimerViewModel.kt`
  - 只持久化 `deadline: Long`（`SavedStateHandle` 存储）
  - 界面用 `deadline - SystemClock.elapsedRealtime()` 反推剩余时间
  - 天然抗重建、抗后台恢复误差
- 新建 `compose/screen/DeadlineTimerScreen.kt`：配套 UI + 说明卡片

### Foreground Service 后台倒计时

- 新建 `service/TimerForegroundService.kt`
  - `startForeground()` + `NotificationCompat` 实时更新剩余时间
  - `LocalBinder` 供 Activity/Compose 绑定获取实时 `StateFlow<TimerState>`
  - 通知栏适配 + `FOREGROUND_SERVICE` / `POST_NOTIFICATIONS` 权限
- 新建 `compose/screen/ForegroundServiceScreen.kt`
  - 绑定 Service 获取状态，圆环进度 + 控制按钮
  - 通知状态指示卡片

### 新增通用资源

- 新建 `drawable/ic_arrow_back.xml`：Material 返回箭头图标

### AndroidManifest + 首页入口

- `AndroidManifest.xml`：注册 `FlowSingleActivity`、`FlowListActivity`、`TimerForegroundService` + 权限
- `activity_main.xml`：新增 "Flow + XML" 分类卡片（2 个入口按钮）
- `MainActivity.java`：注册 Flow 入口按钮点击事件

### MoreScreen + ComposeMainActivity 导航扩展

- `MoreScreen.kt`：新增 3 个入口（声明式倒计时、Deadline 模型、后台 Service）
- `ComposeMainActivity.kt`：新增 3 个子页面路由（declarative / deadline / service）

### 新增/修改文件列表

| 文件 | 操作 |
|------|------|
| `engine/TickerRepository.kt` | 新增 |
| `flow/FlowSingleActivity.kt` | 新增 |
| `flow/FlowListActivity.kt` | 新增 |
| `res/layout/activity_flow_single.xml` | 新增 |
| `res/layout/activity_flow_list.xml` | 新增 |
| `res/layout/list_item_flow.xml` | 新增 |
| `compose/screen/DeclarativeTimerScreen.kt` | 新增 |
| `compose/viewmodel/DeadlineTimerViewModel.kt` | 新增 |
| `compose/screen/DeadlineTimerScreen.kt` | 新增 |
| `service/TimerForegroundService.kt` | 新增 |
| `compose/screen/ForegroundServiceScreen.kt` | 新增 |
| `res/drawable/ic_arrow_back.xml` | 新增 |
| `AndroidManifest.xml` | 修改 |
| `res/layout/activity_main.xml` | 修改 |
| `MainActivity.java` | 修改 |
| `compose/screen/MoreScreen.kt` | 修改 |
| `compose/ComposeMainActivity.kt` | 修改 |
| `CHANGELOG.md` | 修改 |

---

## 2026-03-31 — 新增番茄钟、翻页时钟、技术方案对比 + 导航重构

### 番茄钟（PomodoroScreen + PomodoroViewModel）

- 新建 `compose/viewmodel/PomodoroViewModel.kt`：工作/休息多阶段状态管理，支持 4 轮循环
- 新建 `compose/screen/PomodoroScreen.kt`：圆环进度（工作 Primary / 休息 Tertiary）、阶段指示器、统计卡片（完成数/专注时长/完成率）
- 控制按钮：开始 / 暂停 / 继续 / 结束，Material3 风格

### 翻页时钟（FlipClockScreen）

- 新建 `compose/screen/FlipClockScreen.kt`：上下分割翻页数字效果
- 三种风格切换：经典黑 / 白色 / 彩色（FilterChip 选择）
- 内置 5 分钟倒计时 + 开始/暂停/重置控制
- 使用 Compose `animateFloatAsState` 实现数字翻转动画

### 技术方案对比矩阵（TechComparisonScreen）

- 新建 `compose/screen/TechComparisonScreen.kt`
- 13 种技术方案横向对比表格（精度/后台/生命周期/配置变更/API/推荐场景）
- 6 个场景推荐卡片（简单倒计时/后台计时/闹钟/列表/动画/验证码）
- 支持横向滚动浏览

### MoreScreen 导航重构

- `MoreScreen.kt`：入口项增加 `key` 标识，回调改为 `onNavigate(key: String)`
- `ComposeMainActivity.kt`：`subPage` 从 Int 改为 String 类型，新增 4 个子页面路由（sms / pomodoro / flip / tech）
- 点击底部导航任意 Tab 自动回到主页面（重置 subPage）

### 新增/修改文件列表

| 文件 | 操作 |
|------|------|
| `compose/viewmodel/PomodoroViewModel.kt` | 新增 |
| `compose/screen/PomodoroScreen.kt` | 新增 |
| `compose/screen/FlipClockScreen.kt` | 新增 |
| `compose/screen/TechComparisonScreen.kt` | 新增 |
| `compose/screen/MoreScreen.kt` | 修改 — 导航回调重构 |
| `compose/ComposeMainActivity.kt` | 修改 — 子页面路由扩展 |

---

## 2026-03-31 — 剩余布局 M3 改造 + 代码硬编码颜色修复 + README 更新

### activity_chronometer.xml

- Chronometer 组件居中大字显示 (56sp monospace)
- 按钮统一为 MaterialButton (Filled/Tonal/Outlined)

### 协程 Activity 代码修复

- `CoroutineListActivity.kt` — 列表项颜色从硬编码 (`0xFF3F51B5` 等) 改为引用 `R.color.timer_running/warning/finished`
- `CoroutineSmsActivity.kt` — 结果文本颜色从硬编码改为引用 `R.color.md_primary/md_error/md_tertiary`

### README.md 更新

- 重写为完整的项目百科文档，包含功能概览、UI 设计说明、构建环境、项目结构

---

## 2026-03-31 — 协程端 + 列表端 XML 布局 Material 3 改造

### 协程单个倒计时 (activity_coroutine_single.xml)

- 计时数字居中大字显示 (56sp monospace)，状态文本使用主题色
- `ProgressBar` → `LinearProgressIndicator` (M3)，8dp 圆角轨道
- 按钮统一为 `MaterialButton` (Filled/Tonal/Outlined)

### 协程列表倒计时 (activity_coroutine_list.xml + list_item_coroutine.xml)

- 添加按钮改为 `MaterialButton.TonalButton`
- RecyclerView 增加水平/底部 padding
- 列表项改为 `MaterialCardView` 容器 (16dp 圆角)，内含标题/等宽时间/LinearProgressIndicator

### 协程短信倒计时 (activity_coroutine_sms.xml)

- `EditText` → `TextInputLayout` (OutlinedBox) + `TextInputEditText`
- `MaterialCardView` 表单容器，发送按钮改为 `TonalButton`
- 验证/重置按钮统一为 `MaterialButton`

### RecyclerView 列表布局 (activity_recycler_view.xml)

- RecyclerView 增加水平/垂直 padding，`clipToPadding=false`

### Kotlin SMS 布局 (activity_kotiln_sms.xml)

- 从空白 `ConstraintLayout` 改为完整 M3 表单布局（与 activity_sms.xml 风格统一）

---

## 2026-03-31 — XML 端页面 Material 3 改造

### 首页改造 (activity_main.xml + MainActivity.java)

- 从 `ScrollView` + 平铺 `Button` 改为 `CoordinatorLayout` + `NestedScrollView` + `MaterialCardView` 分类卡片
- `MaterialToolbar` 替代旧 `Toolbar`，标题居中
- 按 5 个分类 (基础计时/列表/短信/协程/Compose) 分组，协程和 Compose 卡片带高亮边框
- 新增 `Widget.CountDown.DemoEntry` 样式 — TextButton 风格的列表入口项
- `MainActivity.java` 移除旧 Toolbar 引用，简化初始化代码

### Toolbar 统一改造 (layout_toolbar.xml)

- 迁移至 `MaterialToolbar`，移除硬编码背景色和文字色

### 通用倒计时布局改造 (activity_common.xml)

- 计时数字居中大字显示 (56sp monospace)
- 按钮改为 `MaterialButton` (Filled/Tonal/Outlined) 带圆角

### ValueAnimator 布局改造 (activity_value_anim.xml)

- `ProgressBar` 替换为 `LinearProgressIndicator` (M3)
- 与 activity_common.xml 统一按钮风格

### SMS 验证码布局改造 (activity_sms.xml)

- 使用 `TextInputLayout` (OutlinedBox) 替代裸 `EditText`
- `MaterialCardView` 表单容器，发送按钮改为 `TonalButton`
- 新增 `ShapeAppearance.CountDown.TextInput` 圆角样式

---

## 2026-03-31 — Material 3 UI 全面升级 + 新增秒表/日期目标功能

### 设计系统与主题升级

- **Compose Theme**: 全新 Light/Dark 配色方案 (Indigo → Blue Violet 演进)，新增 `TimerColors` 语义颜色 (running/paused/warning/finished/stopwatch)，`TimerFontFamily` (Monospace) 等
- **XML 资源**: `colors.xml` 新增 M3 语义色值和 Timer 状态颜色，`dimens.xml` 新增间距/圆角/计时器尺寸 token，`styles.xml` 迁移至 `Theme.Material3.Light.NoActionBar`
- **Typography**: display 层级使用 Monospace 字体，适配计时器场景

### Gradle 依赖升级

- 新增 `com.google.android.material:material:1.12.0` (XML 端 Material3)
- 新增 `androidx.compose.ui:ui-text-google-fonts` (Compose 字体支持)

### 新增 Compose 基础组件库

- `TimerDisplay` — 时间数字显示 (Large/Medium/Small/ListItem 四种尺寸，支持毫秒)
- `CircularTimerProgress` — 圆环进度 (Canvas 绘制，动画过渡)
- `ControlButtonRow` — 按钮行容器 (Idle/Running/Paused/Finished 四种状态自适应)
- `StatusChip` — 状态标签 (就绪/运行中/已暂停/即将结束/已完成)

### 改造已有 Compose 页面

- `SingleCountDownScreen` — 使用新组件重构，新增快捷时长 FilterChip、StatusChip、语义化进度颜色
- `ListCountDownScreen` — 使用 TimerDisplay/StatusChip 组件，TimerCard 带圆角卡片和状态标签
- `SmsCountDownScreen` — Material3 表单风格，LinearProgressIndicator 进度条，图标化结果反馈卡片

### 新增功能页面

- `StopwatchScreen` + `StopwatchViewModel` — 秒表/正计时，基于 `SystemClock.elapsedRealtime()` 高精度，毫秒显示，分段计时 (Lap) 含最快/最慢高亮
- `DateTargetScreen` + `DateTargetViewModel` — 日期目标倒计时，主目标大卡片 (天数 + HH:MM:SS)，次要目标紧凑列表
- `MoreScreen` — 更多功能入口 (短信验证码、番茄钟、翻页时钟、技术对比等占位)

### 导航改造

- `ComposeMainActivity` — 从 3 Tab 扩展为 5 Tab (倒计时/秒表/列表/目标/更多)，CenterAlignedTopAppBar，Crossfade 切换动画，More 子页面导航

---

## 2026-03-31 — 新增协程 + XML 布局倒计时 Demo

### 新增 Kotlin Coroutines + ViewBinding + XML 三大倒计时页面

使用 `lifecycleScope` 协程替代传统 Handler/Timer/CountDownTimer，配合 XML 布局 + ViewBinding 实现，展示现代协程方案在传统 View 体系中的应用。

#### 构建配置变更
- **文件**: `app/build.gradle`
- 添加 `kotlinOptions { jvmTarget = '17' }`
- 添加 `lifecycle-runtime-ktx:2.9.0` 依赖（提供 `lifecycleScope`）

#### 1. 协程单个倒计时 (`CoroutineSingleActivity`)
- **文件**: `coroutine/CoroutineSingleActivity.kt`, `res/layout/activity_coroutine_single.xml`
- 60 秒倒计时，大字体时间显示 + 水平进度条
- 支持开始、暂停、继续、取消操作
- 使用 `lifecycleScope.launch` + `delay` 实现倒计时，Activity 销毁时自动取消

#### 2. 协程多列表倒计时 (`CoroutineListActivity`)
- **文件**: `coroutine/CoroutineListActivity.kt`, `res/layout/activity_coroutine_list.xml`, `res/layout/list_item_coroutine.xml`
- RecyclerView + ViewBinding 展示多个倒计时任务
- **单一协程统一刷新**所有条目（每秒一次），避免每项独立 Timer
- 每项显示名称、剩余时间（等宽字体）、线性进度条
- 剩余 < 10 秒时时间文字变红色
- 顶部按钮可动态添加随机时长新任务

#### 3. 协程短信验证码倒计时 (`CoroutineSmsActivity`)
- **文件**: `coroutine/CoroutineSmsActivity.kt`, `res/layout/activity_coroutine_sms.xml`
- 手机号输入 + 验证码输入 + 发送/验证/重置按钮
- 每次发送随机生成 4 位验证码，60 秒倒计时
- 倒计时期间发送按钮禁用并显示剩余秒数
- 验证结果用不同颜色显示（绿色成功/红色错误/蓝色提示）

#### 集成到现有项目
- **文件**: `AndroidManifest.xml`, `MainActivity.java`, `activity_main.xml`
- 注册 3 个新 Activity
- 首页新增 "协程 + XML (新)" 分组，包含 3 个入口按钮（粉色高亮）

### 新增文件清单

| 文件 | 说明 |
|------|------|
| `coroutine/CoroutineSingleActivity.kt` | 协程单个倒计时 |
| `coroutine/CoroutineListActivity.kt` | 协程多列表倒计时 + Adapter |
| `coroutine/CoroutineSmsActivity.kt` | 协程短信验证码倒计时 |
| `res/layout/activity_coroutine_single.xml` | 单个倒计时布局 |
| `res/layout/activity_coroutine_list.xml` | 列表倒计时布局 |
| `res/layout/activity_coroutine_sms.xml` | 短信倒计时布局 |
| `res/layout/list_item_coroutine.xml` | 列表条目布局（含进度条） |

### 修改的已有文件

| 文件 | 变更 |
|------|------|
| `app/build.gradle` | 添加 kotlinOptions、lifecycle-runtime-ktx |
| `AndroidManifest.xml` | 注册 3 个新 Activity |
| `MainActivity.java` | 添加 3 个入口按钮跳转 |
| `activity_main.xml` | 添加 "协程 + XML" 分组和 3 个按钮 |

---

## 2026-03-31 — 新增 Compose UI 倒计时功能

### 新增 Jetpack Compose 三大倒计时页面

使用 Material3 + ViewModel + Coroutines 实现现代化的 Compose UI 倒计时功能，通过底部导航在三个页面间切换。

#### 构建配置变更
- **文件**: `build.gradle` (根), `app/build.gradle`
- 根 `build.gradle` 添加 `org.jetbrains.kotlin.plugin.compose` 插件声明
- `app/build.gradle` 应用 Compose 插件，启用 `compose true`，添加 `kotlinOptions { jvmTarget = '17' }`
- 添加 Compose BOM (`2026.03.00`)、Material3、Activity Compose、ViewModel Compose、Lifecycle Runtime Compose、Material Icons Extended 依赖

#### 1. 单个倒计时页面 (`SingleCountDownScreen`)
- **文件**: `compose/screen/SingleCountDownScreen.kt`, `compose/viewmodel/SingleCountDownViewModel.kt`
- 圆形进度指示器 + 大字体时间显示（HH:MM:SS）
- 支持开始、暂停、继续、取消操作
- 滑块调节倒计时时长（5~300 秒）
- 倒计时状态通过 `StateFlow` 驱动 UI，配置变更不丢失状态
- 使用 `viewModelScope` 协程替代 Handler/Timer，自动随 ViewModel 生命周期取消

#### 2. 多列表倒计时页面 (`ListCountDownScreen`)
- **文件**: `compose/screen/ListCountDownScreen.kt`, `compose/viewmodel/ListCountDownViewModel.kt`
- `LazyColumn` 展示多个倒计时任务，每项显示名称、剩余时间、线性进度条
- **单一协程统一刷新**所有条目（替代旧方案中每个条目独立 CountDownTimer 的性能问题）
- 剩余 < 10 秒时进度条和时间文字变红色警告
- 已完成的任务卡片变灰色
- 浮动按钮添加新的随机时长任务
- 使用 `key = { it.id }` 确保列表项稳定复用

#### 3. 短信验证码倒计时页面 (`SmsCountDownScreen`)
- **文件**: `compose/screen/SmsCountDownScreen.kt`, `compose/viewmodel/SmsCountDownViewModel.kt`
- 手机号输入 + 验证码输入 + 发送按钮 + 验证按钮
- 60 秒倒计时，倒计时期间发送按钮禁用并显示剩余秒数
- 模拟验证码发送和验证流程（模拟验证码: 123456）
- 结果提示卡片根据状态显示不同颜色（成功/错误/提示）
- 支持重置和取消倒计时

#### 4. Compose 入口 (`ComposeMainActivity`)
- **文件**: `compose/ComposeMainActivity.kt`
- 继承 `ComponentActivity`，使用 `enableEdgeToEdge()` + `setContent` 设置 Compose 内容
- 底部导航栏（`NavigationBar`）切换三个页面：倒计时、多列表、短信验证
- 使用 `rememberSaveable` 保持导航状态

#### 5. 自定义 Material3 主题
- **文件**: `compose/theme/Theme.kt`
- 定义 Light/Dark 两套配色方案，主色调与原项目保持一致（Indigo + Pink）
- 自动跟随系统深色模式

#### 6. 集成到现有项目
- **文件**: `AndroidManifest.xml`, `MainActivity.java`, `activity_main.xml`
- 在 `AndroidManifest.xml` 注册 `ComposeMainActivity`
- 在首页添加 "Compose 倒计时合集" 入口按钮

### 新增文件清单

| 文件 | 说明 |
|------|------|
| `compose/ComposeMainActivity.kt` | Compose 入口 Activity，底部导航 |
| `compose/theme/Theme.kt` | Material3 主题定义 |
| `compose/screen/SingleCountDownScreen.kt` | 单个倒计时页面 |
| `compose/screen/ListCountDownScreen.kt` | 多列表倒计时页面 |
| `compose/screen/SmsCountDownScreen.kt` | 短信验证码倒计时页面 |
| `compose/viewmodel/SingleCountDownViewModel.kt` | 单个倒计时 ViewModel |
| `compose/viewmodel/ListCountDownViewModel.kt` | 多列表倒计时 ViewModel |
| `compose/viewmodel/SmsCountDownViewModel.kt` | 短信验证码 ViewModel |

### 修改的已有文件

| 文件 | 变更 |
|------|------|
| `build.gradle` (根) | 添加 Compose Compiler 插件 |
| `app/build.gradle` | 添加 Compose 依赖、启用 compose 构建特性 |
| `AndroidManifest.xml` | 注册 ComposeMainActivity |
| `MainActivity.java` | 添加 Compose 入口按钮跳转 |
| `activity_main.xml` | 添加 Compose 入口按钮 |

---

## 2026-03-31 — 全面审查与修复

### P0 严重问题修复

#### 1. 修复 `CountDownTextView.getFormatTime()` 硬编码 bug
- **文件**: `widget/CountDownTextView.java`
- **问题**: `getFormatTime()` 方法中 `day/hour/minute/seconds` 全部硬编码为 1，传入的毫秒参数完全未使用，导致 `setAutoDisplayText(true)` 功能完全失效，始终显示 `01:01:01`
- **修复**: 根据传入的毫秒数正确计算天/时/分/秒，并在 `TIME_SHOW_H_M_S` 模式下将天数折算进小时

#### 2. 补充缺失的 `onDestroy()` 生命周期清理
- **文件**: `CountDownSimpleActivity.java`, `listview/CountDownListActivity.java`
- **问题**: Activity 销毁后 `CountDownTimer` 仍在运行，回调尝试更新已销毁的 View，可能导致崩溃或内存泄漏
- **修复**: 在 `onDestroy()` 中调用 `cancel()` / `cancelAllTimers()` 释放资源

#### 3. 修复废弃 Handler 构造函数 + 内存泄漏
- **文件**: `utils/CountDown.java`, `utils/MyCountDownTimer.java`, `utils/CountDownSmsUtil.java`
- **问题**: 使用 `new Handler()` 无参构造函数（Android 11+ 已废弃），隐式绑定调用线程 Looper，非主线程创建会崩溃；非静态内部类 Handler 持有外部引用导致内存泄漏
- **修复**: 统一改为 `new Handler(Looper.getMainLooper())`，并将字段声明为 `final`

#### 4. 修复 `HandlerSimpleActivity` 暂停/恢复状态管理 bug
- **文件**: `HandlerSimpleActivity.java`
- **问题**: 暂停时未设置 `isPause = true`，恢复时未检查 `isPause` 状态也未重置，导致暂停/恢复功能完全失效
- **修复**: 在 start/cancel/pause/resume 各分支中正确维护 `isPause` 状态，resume 增加 `isPause && curTime > 0` 前置条件

---

### P1 重要问题修复

#### 5. 添加 Kotlin 插件配置
- **文件**: `build.gradle` (根), `app/build.gradle`
- **问题**: 项目包含 `SmsKotilnActivity.kt` 但未配置 Kotlin 插件，无法编译
- **修复**: 根 `build.gradle` 添加 `org.jetbrains.kotlin.android` 插件声明，`app/build.gradle` 应用该插件并配置 `kotlinOptions { jvmTarget = '17' }`

#### 6. 修复 `RxJavaActivity` Observer 重复订阅导致 Disposable 丢失
- **文件**: `RxJavaActivity.java`
- **问题**: 同一个 `mObserver` 实例被多次订阅，`onSubscribe` 覆盖 `mDisposable`，旧订阅的 Disposable 丢失无法取消
- **修复**: 提取 `subscribe()` 方法，每次订阅前先 `dispose()` 旧订阅，再创建新的匿名 Observer；移除不再使用的 `mObserver` 字段

#### 7. 修复列表倒计时 `SparseArray` key 使用 `hashCode()` 不可靠
- **文件**: `listview/CountDownRecyclerViewActivity.java`, `listview/CountDownRecyclerViewActivity2.java`, `listview/CountDownListActivity.java`
- **问题**: 使用 `View.hashCode()` 作为 SparseArray key，不保证唯一性，不同 View 可能产生相同 hashCode 导致 Timer 管理混乱
- **修复**: 改用 `position` 作为 key；在 `onBindViewHolder` 中先取消该 position 上可能残留的旧 Timer；移除无用的 `Log.e` 调用

---

### P2 改进优化

#### 8. 补全 `ChronometerActivity` 暂停/恢复功能
- **文件**: `ChronometerActivity.java`
- **问题**: pause 和 resume 按钮注册了点击监听但 `onClick` 中未处理
- **修复**: 暂停时记录 `mTimeLeftOnPause`（剩余毫秒数）并 `stop()`；恢复时用记录的剩余时间重新设置 `base` 并 `start()`；修复 `onChronometerTick` 中的倒计时结束判断逻辑；移除未使用的 `Context`、`Build`、`Log`、`RequiresApi` 引用

#### 9. 补全 `AlarmActivity` 取消功能
- **文件**: `AlarmActivity.java`
- **问题**: cancel 按钮注册了监听但 `onClick` 中未处理
- **修复**: 添加 cancel 分支：取消 CountDownTimer、重置状态为 STOPPED、清除已保存的开始时间

#### 10. 修复 `TimeTools.getCountTimeByLong()` int 溢出风险
- **文件**: `utils/TimeTools.java`
- **问题**: `long` 毫秒值强转为 `int` 秒数，超过约 24.8 天时 int 溢出
- **修复**: 将 `totalTime`、`hour`、`minute`、`second` 全部改为 `long` 类型

#### 11. 修复 `PrefUtils` 使用废弃 API
- **文件**: `utils/PrefUtils.java`
- **问题**: `PreferenceManager.getDefaultSharedPreferences()` 已废弃
- **修复**: 改用 `Context.getSharedPreferences(name, MODE_PRIVATE)`

#### 12. 移除过时的 `fileTree` 依赖声明
- **文件**: `app/build.gradle`
- **问题**: `implementation fileTree(dir: 'libs', include: ['*.jar'])` 是过时的依赖管理方式
- **修复**: 移除该行

#### 13. 修复资源文件命名错误
- **文件**: `res/values/attrs.xml.xml` → `res/values/attrs.xml`
- **问题**: 双重 `.xml` 扩展名可能导致资源解析问题
- **修复**: 重命名为 `attrs.xml`

#### 14. 启用 ViewBinding
- **文件**: `app/build.gradle`
- **修复**: 在 `buildFeatures` 中启用 `viewBinding true`，后续新代码可直接使用 ViewBinding 替代 `findViewById`

---

### 修改文件清单

| 文件 | 变更类型 |
|------|----------|
| `build.gradle` (根) | 添加 Kotlin 插件声明 |
| `app/build.gradle` | 添加 Kotlin 插件、kotlinOptions、ViewBinding、移除 fileTree |
| `app/src/main/java/.../CountDownSimpleActivity.java` | 添加 onDestroy 清理 |
| `app/src/main/java/.../HandlerSimpleActivity.java` | 修复 isPause 状态管理 |
| `app/src/main/java/.../RxJavaActivity.java` | 修复 Observer 重复订阅 |
| `app/src/main/java/.../ChronometerActivity.java` | 补全 pause/resume、清理无用导入 |
| `app/src/main/java/.../AlarmActivity.java` | 补全 cancel 功能 |
| `app/src/main/java/.../utils/CountDown.java` | 修复废弃 Handler |
| `app/src/main/java/.../utils/MyCountDownTimer.java` | 修复废弃 Handler |
| `app/src/main/java/.../utils/CountDownSmsUtil.java` | 修复废弃 Handler |
| `app/src/main/java/.../utils/TimeTools.java` | 修复 int 溢出 |
| `app/src/main/java/.../utils/PrefUtils.java` | 替换废弃 API |
| `app/src/main/java/.../widget/CountDownTextView.java` | 修复 getFormatTime 硬编码 |
| `app/src/main/java/.../listview/CountDownListActivity.java` | 添加 onDestroy、修复 SparseArray key |
| `app/src/main/java/.../listview/CountDownRecyclerViewActivity.java` | 修复 SparseArray key |
| `app/src/main/java/.../listview/CountDownRecyclerViewActivity2.java` | 修复 SparseArray key |
| `app/src/main/res/values/attrs.xml.xml` → `attrs.xml` | 修复文件名 |
