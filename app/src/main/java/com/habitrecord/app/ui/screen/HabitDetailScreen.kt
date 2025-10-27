package com.habitrecord.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitrecord.app.data.model.CheckinEntity
import com.habitrecord.app.data.model.HabitEntity
import com.habitrecord.app.ui.theme.CheckedColor
import com.habitrecord.app.ui.theme.TodayBorderColor
import com.habitrecord.app.ui.theme.UncheckedColor
import com.habitrecord.app.utils.DateUtils
import com.habitrecord.app.utils.HabitIcons
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habit: HabitEntity?,
    totalDays: Int,
    currentYearMonth: YearMonth,
    monthCheckins: List<CheckinEntity>,
    monthCount: Int,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateClick: (String) -> Unit,
    onViewYearStats: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(habit?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "删除")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 习惯信息卡片 - 压缩版本
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = habit?.let {
                        Color(android.graphics.Color.parseColor(it.color)).copy(alpha = 0.2f)
                    } ?: MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = HabitIcons.getIconById(habit?.icon ?: "default"),
                        contentDescription = habit?.name,
                        modifier = Modifier.size(40.dp),
                        tint = habit?.let {
                            Color(android.graphics.Color.parseColor(it.color))
                        } ?: MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "总计 ${totalDays}d",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 月份导航
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "上个月")
                }

                Text(
                    text = "${currentYearMonth.year}年 ${currentYearMonth.monthValue}月",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onNextMonth) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "下个月")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 日历视图
            MonthCalendar(
                yearMonth = currentYearMonth,
                checkedDates = monthCheckins.map { it.date }.toSet(),
                onDateClick = onDateClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 本月统计
            Text(
                text = "本月完成: ${monthCount}d",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 年度统计按钮
            Button(
                onClick = onViewYearStats,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.BarChart, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("查看年度统计")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 编辑按钮
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("编辑习惯")
            }

            // 底部留白，方便滚动
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 删除确认对话框
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("确认删除") },
                text = { Text("确定要删除这个习惯吗？所有打卡记录将被删除。") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            onDelete()
                        }
                    ) {
                        Text("删除", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun MonthCalendar(
    yearMonth: YearMonth,
    checkedDates: Set<String>,
    onDateClick: (String) -> Unit
) {
    val today = LocalDate.now()
    val daysInMonth = DateUtils.getMonthDays(yearMonth.year, yearMonth.monthValue)
    val firstDayOfWeek = daysInMonth.first().dayOfWeek.value % 7 // 转换为0-6，0为周日

    Column {
        // 星期标题
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 日期网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(300.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 前置空白
            items(firstDayOfWeek) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            // 日期
            items(daysInMonth) { date ->
                val dateString = DateUtils.formatDate(date)
                val isChecked = checkedDates.contains(dateString)
                val isToday = date == today
                val isPast = date.isBefore(today)

                DayCell(
                    day = date.dayOfMonth,
                    isChecked = isChecked,
                    isToday = isToday,
                    isPast = isPast,
                    onClick = { if (isPast || isToday) onDateClick(dateString) }
                )
            }
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    isChecked: Boolean,
    isToday: Boolean,
    isPast: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                when {
                    isChecked -> CheckedColor
                    else -> UncheckedColor
                }
            )
            .then(
                if (isToday) Modifier.border(2.dp, TodayBorderColor, CircleShape)
                else Modifier
            )
            .clickable(enabled = isPast || isToday) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = if (isChecked) Color.White else Color.Black,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

