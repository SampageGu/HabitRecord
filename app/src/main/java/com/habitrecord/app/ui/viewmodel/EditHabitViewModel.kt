package com.habitrecord.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.habitrecord.app.data.model.HabitEntity
import com.habitrecord.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 编辑习惯 ViewModel
 */
class EditHabitViewModel(
    private val habitId: Long,
    private val repository: HabitRepository
) : ViewModel() {

    private val _habit = MutableStateFlow<HabitEntity?>(null)
    val habit: StateFlow<HabitEntity?> = _habit.asStateFlow()

    private val _habitName = MutableStateFlow("")
    val habitName: StateFlow<String> = _habitName.asStateFlow()

    private val _selectedIcon = MutableStateFlow("default")
    val selectedIcon: StateFlow<String> = _selectedIcon.asStateFlow()

    private val _selectedColor = MutableStateFlow("#6200EE")
    val selectedColor: StateFlow<String> = _selectedColor.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        loadHabit()
    }

    private fun loadHabit() {
        viewModelScope.launch {
            val habitEntity = repository.getHabitById(habitId)
            habitEntity?.let {
                _habit.value = it
                _habitName.value = it.name
                _selectedIcon.value = it.icon
                _selectedColor.value = it.color
            }
        }
    }

    fun updateName(name: String) {
        _habitName.value = name
    }

    fun selectIcon(iconId: String) {
        _selectedIcon.value = iconId
    }

    fun selectColor(color: String) {
        _selectedColor.value = color
    }

    fun updateHabit(onSuccess: () -> Unit) {
        val currentHabit = _habit.value
        if (_habitName.value.isBlank() || currentHabit == null) {
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            try {
                val updatedHabit = currentHabit.copy(
                    name = _habitName.value.trim(),
                    icon = _selectedIcon.value,
                    color = _selectedColor.value,
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateHabit(updatedHabit)
                onSuccess()
            } finally {
                _isSaving.value = false
            }
        }
    }
}

class EditHabitViewModelFactory(
    private val habitId: Long,
    private val repository: HabitRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditHabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditHabitViewModel(habitId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

