package com.example.wealthwise.presentation.features.tasks.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wealthwise.domain.model.FinancialTask
import com.example.wealthwise.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val task: FinancialTask? = null
)

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailsUiState(isLoading = true))
    val uiState: StateFlow<TaskDetailsUiState> = _uiState

    init {
        val taskId = savedStateHandle.get<Long>("taskId")
        if (taskId != null) {
            loadTask(taskId)
        } else {
            _uiState.value = TaskDetailsUiState(error = "Task ID not found")
        }
    }

    private fun loadTask(taskId: Long) {
        viewModelScope.launch {
            try {
                taskRepository.getAllTasks().collect { tasks ->
                    val task = tasks.find { it.id == taskId }
                    if (task != null) {
                        _uiState.value = TaskDetailsUiState(task = task)
                    } else {
                        _uiState.value = TaskDetailsUiState(error = "Task not found")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = TaskDetailsUiState(error = e.message ?: "Failed to load task")
            }
        }
    }

    fun toggleTaskCompletion() {
        val currentTask = _uiState.value.task ?: return
        viewModelScope.launch {
            try {
                taskRepository.updateTaskCompletionStatus(
                    taskId = currentTask.id,
                    isCompleted = !currentTask.isCompleted
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to update task")
            }
        }
    }

    fun deleteTask() {
        val currentTask = _uiState.value.task ?: return
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(currentTask)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to delete task")
            }
        }
    }
} 