package com.habitrecord.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.habitrecord.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 创建/编辑习惯 ViewModel
 */
class CreateHabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val _habitName = MutableStateFlow("")
    val habitName: StateFlow<String> = _habitName.asStateFlow()

    private val _selectedIcon = MutableStateFlow("default")
    val selectedIcon: StateFlow<String> = _selectedIcon.asStateFlow()

    private val _selectedColor = MutableStateFlow("#6200EE")
    val selectedColor: StateFlow<String> = _selectedColor.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun updateName(name: String) {
        _habitName.value = name
    }

    fun selectIcon(iconId: String) {
        _selectedIcon.value = iconId
    }

    fun selectColor(color: String) {
        _selectedColor.value = color
    }

    fun createHabit(onSuccess: () -> Unit) {
        if (_habitName.value.isBlank()) {
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            try {
                repository.createHabit(
                    name = _habitName.value.trim(),
                    icon = _selectedIcon.value,
                    color = _selectedColor.value
                )
                onSuccess()
            } finally {
                _isSaving.value = false
            }
        }
    }
}

class CreateHabitViewModelFactory(
    private val repository: HabitRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateHabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateHabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

