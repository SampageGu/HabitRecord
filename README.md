# 日常习惯打卡 App

这是一个帮助用户跟踪和记录日常习惯的 Android 原生应用。

## 功能特性

### 核心功能
- ✅ **习惯管理**：创建、编辑、删除习惯
- ✅ **今日打卡**：在首页快速完成/取消今日打卡
- ✅ **月度日历**：可视化查看每月打卡情况
- ✅ **年度统计**：展示过去12个月的打卡统计
- ✅ **补卡功能**：支持补签历史日期
- ✅ **图标库**：内置多种习惯图标供选择
- ✅ **本地存储**：完全离线使用，数据存储在本地

### 技术栈
- **语言**：Kotlin
- **UI框架**：Jetpack Compose
- **数据库**：Room
- **架构**：MVVM + Repository Pattern
- **导航**：Navigation Compose
- **异步处理**：Kotlin Coroutines + Flow

## 项目结构

```
app/
├── data/
│   ├── model/          # 数据模型（Entity）
│   ├── dao/            # 数据访问对象
│   ├── database/       # Room 数据库
│   └── repository/     # 数据仓库
├── ui/
│   ├── screen/         # UI 界面
│   ├── viewmodel/      # ViewModel
│   └── theme/          # 主题配置
└── utils/              # 工具类
```

## 数据库设计

### habits 表
- id: 习惯ID
- name: 习惯名称
- icon: 图标ID
- color: 颜色
- created_at: 创建时间
- updated_at: 更新时间
- order_index: 显示顺序
- is_archived: 是否归档

### checkins 表
- id: 打卡记录ID
- habit_id: 关联的习惯ID
- date: 打卡日期（YYYY-MM-DD）
- timestamp: 打卡时间戳

## 使用说明

1. **创建习惯**：点击首页右下角的 "+" 按钮
2. **完成打卡**：在首页点击习惯卡片即可完成今日打卡
3. **取消打卡**：再次点击已打卡的习惯卡片可取消
4. **查看详情**：长按习惯卡片进入详情页
5. **补卡**：在详情页的日历中点击过去的日期进行补卡
6. **年度统计**：在详情页点击"查看年度统计"按钮

## 构建运行

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 17
- Android SDK 34
- 最低支持 Android 7.0 (API 24)

### 运行步骤
1. 使用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 连接 Android 设备或启动模拟器
4. 点击 Run 按钮运行应用

## 测试

### 运行单元测试
```bash
./gradlew test
```

### 运行集成测试
```bash
./gradlew connectedAndroidTest
```

## 隐私说明

本应用完全离线运行，所有数据仅保存在设备本地，不会上传到任何服务器。

## 后续计划

- [ ] 习惯提醒通知
- [ ] 云备份同步
- [ ] 数据导出/导入
- [ ] 成就系统
- [ ] 数据可视化增强
- [ ] 多语言支持

## 开源协议

MIT License

## 作者

Habit Record Team

