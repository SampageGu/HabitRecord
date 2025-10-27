package com.habitrecord.app.data.repository

import com.habitrecord.app.data.dao.CheckinDao
import com.habitrecord.app.data.dao.HabitDao
import com.habitrecord.app.data.model.CheckinEntity
import com.habitrecord.app.data.model.HabitEntity
import com.habitrecord.app.data.model.HabitWithStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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

    // 获取所有习惯（带统计信息）- 优化版本，使用 Flow 组合实现响应式更新
    fun getAllHabitsWithStats(): Flow<List<HabitWithStats>> {
        val today = LocalDate.now().format(dateFormatter)

        // 组合习惯列表和今日打卡记录，实现响应式更新
        return combine(
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

    // 切换今日打卡状态
    suspend fun toggleTodayCheckin(habitId: Long): Boolean {
        val today = LocalDate.now().format(dateFormatter)
        val exists = checkinDao.exists(habitId, today)

        return if (exists) {
            checkinDao.deleteByHabitAndDate(habitId, today)
            false // 返回false表示取消打卡
        } else {
            checkinDao.insert(CheckinEntity(habitId = habitId, date = today))
            true // 返回true表示完成打卡
        }
    }

    // 添加打卡记录（补卡）
    suspend fun addCheckin(habitId: Long, date: String) {
        checkinDao.insert(CheckinEntity(habitId = habitId, date = date))
    }

    // 删除打卡记录
    suspend fun deleteCheckin(habitId: Long, date: String) {
        checkinDao.deleteByHabitAndDate(habitId, date)
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

