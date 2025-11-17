package com.habitrecord.app.data.repository

import com.habitrecord.app.data.dao.CheckinDao
import com.habitrecord.app.data.dao.HabitDao
import com.habitrecord.app.data.model.CheckinEntity
import com.habitrecord.app.data.model.HabitEntity
import com.habitrecord.app.data.model.HabitWithStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 习惯数据仓库
 */
class HabitRepository(
    private val habitDao: HabitDao,
    private val checkinDao: CheckinDao
) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val checkinMutex = Mutex() // 添加互斥锁防止并发问题
    private val dateChangeTrigger = MutableStateFlow(0L) // 用于触发日期变化的信号

    // 通知日期已改变
    fun notifyDateChanged() {
        dateChangeTrigger.value = System.currentTimeMillis()
    }

    // 获取所有习惯（带统计信息）- 优化版本，使用 Flow 组合实现响应式更新
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllHabitsWithStats(): Flow<List<HabitWithStats>> {
        // 当日期变化触发器改变时，重新获取最新的今日打卡记录
        return dateChangeTrigger.flatMapLatest {
            val today = LocalDate.now().format(dateFormatter)

            // 组合习惯列表和今日打卡记录
            combine(
                habitDao.getAllHabits(),
                checkinDao.getCheckinsByDate(today)
            ) { habits, todayCheckins ->
                val todayCheckinHabitIds = todayCheckins.map { it.habitId }.toSet()

                habits.map { habit ->
                    val totalDays = checkinDao.countByHabit(habit.id)
                    val checkedToday = todayCheckinHabitIds.contains(habit.id)
                    HabitWithStats(habit, totalDays, checkedToday)
                }
            }
        }
    }

    // 创建习惯
    suspend fun createHabit(name: String, icon: String, color: String): Long {
        val habit = HabitEntity(
            name = name,
            icon = icon,
            color = color,
            orderIndex = 0
        )
        return habitDao.insert(habit)
    }

    // 更新习惯
    suspend fun updateHabit(habit: HabitEntity) {
        habitDao.update(habit.copy(updatedAt = System.currentTimeMillis()))
    }

    // 删除习惯
    suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.delete(habit)
    }

    // 获取习惯详情
    suspend fun getHabitById(habitId: Long): HabitEntity? {
        return habitDao.getHabitById(habitId)
    }

    // 切换今日打卡状态（使用互斥锁确保原子性）
    suspend fun toggleTodayCheckin(habitId: Long): Boolean {
        return checkinMutex.withLock {
            val today = LocalDate.now().format(dateFormatter)
            val existingCheckin = checkinDao.getCheckinByHabitAndDate(habitId, today)

            if (existingCheckin != null) {
                // 已打卡，删除记录
                checkinDao.delete(existingCheckin)
                false // 返回false表示取消打卡
            } else {
                // 未打卡，添加记录
                val checkin = CheckinEntity(
                    habitId = habitId,
                    date = today,
                    timestamp = System.currentTimeMillis()
                )
                checkinDao.insert(checkin)
                true // 返回true表示完成打卡
            }
        }
    }

    // 添加打卡记录（补卡）
    suspend fun addCheckin(habitId: Long, date: String) {
        checkinMutex.withLock {
            val existingCheckin = checkinDao.getCheckinByHabitAndDate(habitId, date)
            if (existingCheckin == null) {
                checkinDao.insert(CheckinEntity(
                    habitId = habitId,
                    date = date,
                    timestamp = System.currentTimeMillis()
                ))
            }
        }
    }

    // 删除打卡记录
    suspend fun deleteCheckin(habitId: Long, date: String) {
        checkinMutex.withLock {
            checkinDao.deleteByHabitAndDate(habitId, date)
        }
    }

    // 获取指定月份的打卡记录
    suspend fun getMonthCheckins(habitId: Long, year: Int, month: Int): List<CheckinEntity> {
        val startDate = LocalDate.of(year, month, 1).format(dateFormatter)
        val endDate = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1).format(dateFormatter)
        return checkinDao.getCheckinsByHabitAndDateRange(habitId, startDate, endDate)
    }

    // 获取指定月份的打卡天数
    suspend fun getMonthCheckinCount(habitId: Long, year: Int, month: Int): Int {
        val startDate = LocalDate.of(year, month, 1).format(dateFormatter)
        val endDate = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1).format(dateFormatter)
        return checkinDao.countByHabitAndDateRange(habitId, startDate, endDate)
    }

    // 获取总打卡天数
    suspend fun getTotalCheckinCount(habitId: Long): Int {
        return checkinDao.countByHabit(habitId)
    }

    // 更新习惯排序
    suspend fun updateHabitOrders(habitIds: List<Long>) {
        habitDao.updateHabitOrders(habitIds)
    }
}

