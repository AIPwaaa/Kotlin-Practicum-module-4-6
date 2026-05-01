package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.TodoJsonDataSource
import com.example.myapplication.data.preferences.UserPreferencesRepository
import com.example.myapplication.data.repository.TodoRepositoryImpl
import com.example.myapplication.domain.model.TodoItem
import com.example.myapplication.domain.usecase.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(
    private val getTodosUseCase: GetTodosUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val repository: TodoRepositoryImpl,
    private val jsonDataSource: TodoJsonDataSource
) : ViewModel() {

    init {
        viewModelScope.launch {
            // Вызываем тот самый разовый импорт
            repository.checkAndImportData(jsonDataSource)
        }
    }

    val uiState: StateFlow<TodoUiState> = combine(
        getTodosUseCase(),
        userPreferencesRepository.isColorEnabled
    ) { tasks, isColorEnabled ->
        TodoUiState(tasks, isColorEnabled)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoUiState()
    )

    fun onTaskCheckedChange(item: TodoItem) {
        viewModelScope.launch {
            updateTaskUseCase(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun toggleColorPreference(isEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateColorPreference(isEnabled)
        }
    }
    fun addTask(item: TodoItem) {
        viewModelScope.launch {
            addTaskUseCase(item)
        }
    }
    fun deleteTask(item: TodoItem) {
        viewModelScope.launch {
            deleteTaskUseCase(item)
        }
    }
}

data class TodoUiState(
    val tasks: List<TodoItem> = emptyList(),
    val isColorEnabled: Boolean = false
)