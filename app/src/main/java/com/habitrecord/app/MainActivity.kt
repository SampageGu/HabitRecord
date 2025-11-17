package com.habitrecord.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.habitrecord.app.data.database.HabitDatabase
import com.habitrecord.app.data.repository.HabitRepository
import com.habitrecord.app.ui.screen.*
import com.habitrecord.app.ui.theme.HabitRecordTheme
import com.habitrecord.app.ui.viewmodel.*
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    private lateinit var repository: HabitRepository
    private var lastDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化数据库和仓库
        val database = HabitDatabase.getDatabase(applicationContext)
        repository = HabitRepository(database.habitDao(), database.checkinDao())

        setContent {
            HabitRecordTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HabitApp(repository = repository)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 检查日期是否变化
        val currentDate = LocalDate.now()
        if (lastDate != null && lastDate != currentDate) {
            // 日期已改变，通知 repository 刷新数据
            repository.notifyDateChanged()
        }
        lastDate = currentDate
    }
}

@Composable
fun HabitApp(repository: HabitRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        // 首页
        composable("home") {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(repository)
            )
            val habits by viewModel.habits.collectAsState()

            HomeScreen(
                habits = habits,
                onHabitClick = { habitId ->
                    // 这里不需要做任何事，因为点击是切换打卡
                },
                onHabitLongClick = { habitId ->
                    navController.navigate("detail/$habitId")
                },
                onAddHabit = {
                    navController.navigate("create")
                },
                onToggleCheckin = { habitId ->
                    viewModel.toggleCheckin(habitId)
                }
            )
        }

        // 创建习惯
        composable("create") {
            val viewModel: CreateHabitViewModel = viewModel(
                factory = CreateHabitViewModelFactory(repository)
            )
            val habitName by viewModel.habitName.collectAsState()
            val selectedIcon by viewModel.selectedIcon.collectAsState()
            val selectedColor by viewModel.selectedColor.collectAsState()

            CreateHabitScreen(
                habitName = habitName,
                selectedIcon = selectedIcon,
                selectedColor = selectedColor,
                onNameChange = { viewModel.updateName(it) },
                onIconSelect = { viewModel.selectIcon(it) },
                onColorSelect = { viewModel.selectColor(it) },
                onSave = {
                    viewModel.createHabit {
                        navController.popBackStack()
                    }
                },
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }

        // 习惯详情
        composable(
            route = "detail/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: return@composable
            val viewModel: HabitDetailViewModel = viewModel(
                factory = HabitDetailViewModelFactory(habitId, repository)
            )

            val habit by viewModel.habit.collectAsState()
            val totalDays by viewModel.totalDays.collectAsState()
            val currentYearMonth by viewModel.currentYearMonth.collectAsState()
            val monthCheckins by viewModel.monthCheckins.collectAsState()
            val monthCount by viewModel.monthCount.collectAsState()

            HabitDetailScreen(
                habit = habit,
                totalDays = totalDays,
                currentYearMonth = currentYearMonth,
                monthCheckins = monthCheckins,
                monthCount = monthCount,
                onBack = { navController.popBackStack() },
                onDelete = {
                    viewModel.deleteHabit {
                        navController.popBackStack()
                    }
                },
                onEdit = {
                    navController.navigate("edit/$habitId")
                },
                onPreviousMonth = { viewModel.previousMonth() },
                onNextMonth = { viewModel.nextMonth() },
                onDateClick = { date ->
                    // 切换补卡状态
                    val isChecked = monthCheckins.any { it.date == date }
                    if (isChecked) {
                        viewModel.deleteCheckin(date)
                    } else {
                        viewModel.addCheckin(date)
                    }
                },
                onViewYearStats = {
                    navController.navigate("yearStats/$habitId")
                }
            )
        }

        // 编辑习惯
        composable(
            route = "edit/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: return@composable
            val viewModel: EditHabitViewModel = viewModel(
                factory = EditHabitViewModelFactory(habitId, repository)
            )

            val habitName by viewModel.habitName.collectAsState()
            val selectedIcon by viewModel.selectedIcon.collectAsState()
            val selectedColor by viewModel.selectedColor.collectAsState()

            EditHabitScreen(
                habitName = habitName,
                selectedIcon = selectedIcon,
                selectedColor = selectedColor,
                onNameChange = { viewModel.updateName(it) },
                onIconSelect = { viewModel.selectIcon(it) },
                onColorSelect = { viewModel.selectColor(it) },
                onSave = {
                    viewModel.updateHabit {
                        navController.popBackStack()
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // 年度统计
        composable(
            route = "yearStats/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: return@composable
            val viewModel: YearStatsViewModel = viewModel(
                factory = YearStatsViewModelFactory(habitId, repository)
            )

            val habitName by viewModel.habitName.collectAsState()
            val yearStats by viewModel.yearStats.collectAsState()

            YearStatsScreen(
                habitName = habitName,
                yearStats = yearStats,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

