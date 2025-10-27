package com.habitrecord.app.data.dao

import androidx.room.*
import com.habitrecord.app.data.model.HabitEntity
import kotlinx.coroutines.flow.Flow

/**
 * 习惯数据访问对象
 */
@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE is_archived = 0 ORDER BY order_index ASC, created_at DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: Long): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteById(habitId: Long)

    @Query("UPDATE habits SET order_index = :orderIndex WHERE id = :habitId")
    suspend fun updateOrder(habitId: Long, orderIndex: Int)

    @Transaction
    suspend fun updateHabitOrders(habitIds: List<Long>) {
        habitIds.forEachIndexed { index, habitId ->
            updateOrder(habitId, index)
        }
    }
}

