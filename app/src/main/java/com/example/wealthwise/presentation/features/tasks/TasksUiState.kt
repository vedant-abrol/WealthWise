package com.example.wealthwise.presentation.features.tasks

import com.example.wealthwise.domain.model.FinancialTask

sealed class TasksUiState {
    object Loading : TasksUiState()
    data class Success(val tasks: List<FinancialTask>) : TasksUiState()
    data class Error(val message: String) : TasksUiState()
} 