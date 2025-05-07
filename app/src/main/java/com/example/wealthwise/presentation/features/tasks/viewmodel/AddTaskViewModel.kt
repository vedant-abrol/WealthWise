package com.example.wealthwise.presentation.features.tasks.viewmodel

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
import java.time.LocalDateTime
import javax.inject.Inject

data class AddTaskUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState

    fun createTask(
        title: String,
        description: String?,
        amount: String,
        dueDate: LocalDate,
        category: String,
        isRecurring: Boolean,
        recurringPeriod: RecurringPeriod?
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = AddTaskUiState(isLoading = true)
                
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

                val task = FinancialTask(
                    title = title,
                    description = description.takeIf { !it.isNullOrBlank() },
                    amount = amountDecimal,
                    dueDate = dueDate,
                    category = category,
                    isRecurring = isRecurring,
                    recurringPeriod = recurringPeriod,
                    createdAt = LocalDateTime.now()
                )

                taskRepository.insertTask(task)
                _uiState.value = AddTaskUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = AddTaskUiState(error = e.message ?: "Failed to create task")
            }
        }
    }

    fun resetState() {
        _uiState.value = AddTaskUiState()
    }
} 