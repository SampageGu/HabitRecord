package com.habitrecord.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 习惯实体
 */
@Entity(
    tableName = "habits",
    indices = [Index(value = ["name"]), Index(value = ["order_index"])]
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "icon")
    val icon: String = "default",

    @ColumnInfo(name = "color")
    val color: String = "#6200EE",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0,

    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false
)

