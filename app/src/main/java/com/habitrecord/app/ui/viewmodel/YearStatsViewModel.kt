package com.habitrecord.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.habitrecord.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

/**
 * 年度统计 ViewModel
 */
class YearStatsViewModel(
    private val habitId: Long,
    private val repository: HabitRepository
) : ViewModel() {

    private val _habitName = MutableStateFlow("")
    val habitName: StateFlow<String> = _habitName.asStateFlow()

    private val _yearStats = MutableStateFlow<Map<YearMonth, Int>>(emptyMap())
    val yearStats: StateFlow<Map<YearMonth, Int>> = _yearStats.asStateFlow()

    init {
        loadYearStats()
    }

    private fun loadYearStats() {
        viewModelScope.launch {
            val habit = repository.getHabitById(habitId)
            _habitName.value = habit?.name ?: ""

            // 获取过去12个月的统计
            val currentYearMonth = YearMonth.now()
            val statsMap = mutableMapOf<YearMonth, Int>()

            for (i in 0..11) {
                val yearMonth = currentYearMonth.minusMonths(i.toLong())
                val count = repository.getMonthCheckinCount(
                    habitId,
                    yearMonth.year,
                    yearMonth.monthValue
                )
                statsMap[yearMonth] = count
            }

            _yearStats.value = statsMap
        }
    }
}

class YearStatsViewModelFactory(
    private val habitId: Long,
    private val repository: HabitRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(YearStatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return YearStatsViewModel(habitId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

