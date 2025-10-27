package com.habitrecord.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 打卡记录实体
 */
@Entity(
    tableName = "checkins",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habit_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habit_id"]),
        Index(value = ["habit_id", "date"], unique = true)
    ]
)
data class CheckinEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "habit_id")
    val habitId: Long,

    @ColumnInfo(name = "date")
    val date: String, // Format: YYYY-MM-DD

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

