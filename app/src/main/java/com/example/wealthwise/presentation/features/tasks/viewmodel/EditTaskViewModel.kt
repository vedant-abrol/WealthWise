package com.example.wealthwise.presentation.features.tasks.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wealthwise.domain.model.FinancialTask
import com.example.wealthwise.domain.model.RecurringPeriod
import com.example.wealthwise.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

data class EditTaskUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val task: FinancialTask? = null
)

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTaskUiState(isLoading = true))
    val uiState: StateFlow<EditTaskUiState> = _uiState

    init {
        val taskId = savedStateHandle.get<Long>("taskId")
        if (taskId != null) {
            loadTask(taskId)
        } else {
            _uiState.value = EditTaskUiState(error = "Task ID not found")
        }
    }

    private fun loadTask(taskId: Long) {
        viewModelScope.launch {
            try {
                taskRepository.getAllTasks().collect { tasks ->
                    val task = tasks.find { it.id == taskId }
                    if (task != null) {
                        _uiState.value = EditTaskUiState(task = task)
                    } else {
                        _uiState.value = EditTaskUiState(error = "Task not found")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = EditTaskUiState(error = e.message ?: "Failed to load task")
            }
        }
    }

    fun updateTask(
        title: String,
        description: String?,
        amount: String,
        dueDate: LocalDate,
        category: String,
        isRecurring: Boolean,
        recurringPeriod: RecurringPeriod?
    ) {
        val currentTask = _uiState.value.task ?: return
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Validate inputs
                if (title.isBlank()) {
                    throw IllegalArgumentException("Title cannot be empty")
                }
                
                val amountDecimal = try {
                    BigDecimal(amount)
                } catch (e: NumberFormatException) {
                    throw IllegalArgumentException("Invalid amount format")
                }
                
                if (category.isBlank()) {
                    throw IllegalArgumentException("Category cannot be empty")
                }
                
                if (isRecurring && recurringPeriod == null) {
                    throw IllegalArgumentException("Recurring period must be selected for recurring tasks")
                }

                val updatedTask = currentTask.copy(
                    title = title,
                    description = description.takeIf { !it.isNullOrBlank() },
                    amount = amountDecimal,
                    dueDate = dueDate,
                    category = category,
                    isRecurring = isRecurring,
                    recurringPeriod = recurringPeriod
                )

                taskRepository.updateTask(updatedTask)
                _uiState.value = _uiState.value.copy(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to update task",
                    isLoading = false
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = _uiState.value.copy(error = null, isSuccess = false)
    }
} 