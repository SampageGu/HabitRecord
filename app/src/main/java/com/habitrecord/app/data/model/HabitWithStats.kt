package com.habitrecord.app.data.model

/**
 * 习惯与打卡统计数据
 */
data class HabitWithStats(
    val habit: HabitEntity,
    val totalDays: Int,
    val checkedToday: Boolean
)

