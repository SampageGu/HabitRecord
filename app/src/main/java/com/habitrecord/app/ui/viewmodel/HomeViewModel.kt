package com.habitrecord.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.habitrecord.app.data.model.HabitEntity
import com.habitrecord.app.data.model.HabitWithStats
import com.habitrecord.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 首页 ViewModel
 */
class HomeViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val _habits = MutableStateFlow<List<HabitWithStats>>(emptyList())
    val habits: StateFlow<List<HabitWithStats>> = _habits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllHabitsWithStats().collect { habitList ->
                _habits.value = habitList
                _isLoading.value = false
            }
        }
    }

    fun toggleCheckin(habitId: Long) {
        viewModelScope.launch {
            repository.toggleTodayCheckin(habitId)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }
}

class HomeViewModelFactory(
    private val repository: HabitRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

