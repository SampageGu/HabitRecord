package com.habitrecord.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.habitrecord.app.data.dao.CheckinDao
import com.habitrecord.app.data.dao.HabitDao
import com.habitrecord.app.data.model.CheckinEntity
import com.habitrecord.app.data.model.HabitEntity

/**
 * Room 数据库
 */
@Database(
    entities = [HabitEntity::class, CheckinEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun checkinDao(): CheckinDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

