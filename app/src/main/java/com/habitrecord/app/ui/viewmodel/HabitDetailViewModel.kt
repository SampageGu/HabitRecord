package com.habitrecord.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.habitrecord.app.data.model.CheckinEntity
import com.habitrecord.app.data.model.HabitEntity
import com.habitrecord.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

/**
 * 习惯详情 ViewModel
 */
class HabitDetailViewModel(
    private val habitId: Long,
    private val repository: HabitRepository
) : ViewModel() {

    private val _habit = MutableStateFlow<HabitEntity?>(null)
    val habit: StateFlow<HabitEntity?> = _habit.asStateFlow()

    private val _totalDays = MutableStateFlow(0)
    val totalDays: StateFlow<Int> = _totalDays.asStateFlow()

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()

    private val _monthCheckins = MutableStateFlow<List<CheckinEntity>>(emptyList())
    val monthCheckins: StateFlow<List<CheckinEntity>> = _monthCheckins.asStateFlow()

    private val _monthCount = MutableStateFlow(0)
    val monthCount: StateFlow<Int> = _monthCount.asStateFlow()

    init {
        loadHabitDetail()
        loadMonthData()
    }

    private fun loadHabitDetail() {
        viewModelScope.launch {
            _habit.value = repository.getHabitById(habitId)
            _totalDays.value = repository.getTotalCheckinCount(habitId)
        }
    }

    private fun loadMonthData() {
        viewModelScope.launch {
            val yearMonth = _currentYearMonth.value
            _monthCheckins.value = repository.getMonthCheckins(
                habitId,
                yearMonth.year,
                yearMonth.monthValue
            )
            _monthCount.value = repository.getMonthCheckinCount(
                habitId,
                yearMonth.year,
                yearMonth.monthValue
            )
        }
    }

    fun previousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
        loadMonthData()
    }

    fun nextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
        loadMonthData()
    }

    fun addCheckin(date: String) {
        viewModelScope.launch {
            repository.addCheckin(habitId, date)
            loadMonthData()
            loadHabitDetail()
        }
    }

    fun deleteCheckin(date: String) {
        viewModelScope.launch {
            repository.deleteCheckin(habitId, date)
            loadMonthData()
            loadHabitDetail()
        }
    }

    fun deleteHabit(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _habit.value?.let {
                repository.deleteHabit(it)
                onDeleted()
            }
        }
    }
}

class HabitDetailViewModelFactory(
    private val habitId: Long,
    private val repository: HabitRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitDetailViewModel(habitId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

