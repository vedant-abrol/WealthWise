package com.example.wealthwise.presentation.features.tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wealthwise.domain.repository.TaskRepository
import com.example.wealthwise.presentation.features.tasks.TasksUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TasksUiState>(TasksUiState.Loading)
    val uiState: StateFlow<TasksUiState> = _uiState

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = TasksUiState.Loading // Explicitly set loading state
            taskRepository.getAllTasks()
                .catch { e ->
                    _uiState.value = TasksUiState.Error("Failed to load tasks: ${e.localizedMessage}")
                }
                .collect { tasks ->
                    _uiState.value = TasksUiState.Success(tasks)
                }
        }
    }

    fun completeTask(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                taskRepository.updateTaskCompletionStatus(taskId, isCompleted)
                // Optionally, you might want to refresh the tasks list or update the specific task in the UI state
            } catch (e: Exception) {
                // Handle error, perhaps show a snackbar or update UI state to reflect error
                _uiState.value = TasksUiState.Error("Failed to update task: ${e.localizedMessage}")
            }
        }
    }
} 