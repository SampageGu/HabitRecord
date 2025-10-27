package com.habitrecord.app

import com.habitrecord.app.data.dao.CheckinDao
import com.habitrecord.app.data.dao.HabitDao
import com.habitrecord.app.data.database.HabitDatabase
import com.habitrecord.app.data.model.CheckinEntity
import com.habitrecord.app.data.model.HabitEntity
import com.habitrecord.app.data.repository.HabitRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class HabitRepositoryTest {

    private lateinit var database: HabitDatabase
    private lateinit var habitDao: HabitDao
    private lateinit var checkinDao: CheckinDao
    private lateinit var repository: HabitRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HabitDatabase::class.java
        ).allowMainThreadQueries().build()

        habitDao = database.habitDao()
        checkinDao = database.checkinDao()
        repository = HabitRepository(habitDao, checkinDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testCreateHabit() = runBlocking {
        val habitId = repository.createHabit("健身", "fitness", "#FF0000")
        assertTrue(habitId > 0)

        val habit = repository.getHabitById(habitId)
        assertNotNull(habit)
        assertEquals("健身", habit?.name)
        assertEquals("fitness", habit?.icon)
    }

    @Test
    fun testToggleCheckin() = runBlocking {
        val habitId = repository.createHabit("跑步", "run", "#00FF00")

        // 第一次打卡
        val result1 = repository.toggleTodayCheckin(habitId)
        assertTrue(result1)

        val count1 = repository.getTotalCheckinCount(habitId)
        assertEquals(1, count1)

        // 取消打卡
        val result2 = repository.toggleTodayCheckin(habitId)
        assertFalse(result2)

        val count2 = repository.getTotalCheckinCount(habitId)
        assertEquals(0, count2)
    }

    @Test
    fun testDeleteHabit() = runBlocking {
        val habitId = repository.createHabit("阅读", "book", "#0000FF")
        repository.toggleTodayCheckin(habitId)

        val habit = repository.getHabitById(habitId)
        assertNotNull(habit)

        repository.deleteHabit(habit!!)

        val deletedHabit = repository.getHabitById(habitId)
        assertNull(deletedHabit)
    }
}

