package com.habitrecord.app.data.dao

import androidx.room.*
import com.habitrecord.app.data.model.CheckinEntity
import kotlinx.coroutines.flow.Flow

/**
 * 打卡记录数据访问对象
 */
@Dao
interface CheckinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkin: CheckinEntity): Long

    @Delete
    suspend fun delete(checkin: CheckinEntity)

    @Query("DELETE FROM checkins WHERE habit_id = :habitId AND date = :date")
    suspend fun deleteByHabitAndDate(habitId: Long, date: String)

    @Query("SELECT EXISTS(SELECT 1 FROM checkins WHERE habit_id = :habitId AND date = :date)")
    suspend fun exists(habitId: Long, date: String): Boolean

    @Query("SELECT * FROM checkins WHERE habit_id = :habitId ORDER BY date DESC")
    fun getCheckinsByHabit(habitId: Long): Flow<List<CheckinEntity>>

    @Query("SELECT * FROM checkins WHERE date = :date")
    fun getCheckinsByDate(date: String): Flow<List<CheckinEntity>>

    @Query("SELECT * FROM checkins WHERE habit_id = :habitId AND date >= :startDate AND date <= :endDate ORDER BY date ASC")
    suspend fun getCheckinsByHabitAndDateRange(
        habitId: Long,
        startDate: String,
        endDate: String
    ): List<CheckinEntity>

    @Query("SELECT COUNT(*) FROM checkins WHERE habit_id = :habitId")
    suspend fun countByHabit(habitId: Long): Int

    @Query("SELECT COUNT(*) FROM checkins WHERE habit_id = :habitId AND date >= :startDate AND date <= :endDate")
    suspend fun countByHabitAndDateRange(
        habitId: Long,
        startDate: String,
        endDate: String
    ): Int

    @Query("SELECT * FROM checkins WHERE habit_id = :habitId AND date = :date LIMIT 1")
    suspend fun getCheckinByHabitAndDate(habitId: Long, date: String): CheckinEntity?
}

