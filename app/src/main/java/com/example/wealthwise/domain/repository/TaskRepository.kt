package com.example.wealthwise.domain.repository

import com.example.wealthwise.domain.model.FinancialTask
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskRepository {
    fun getAllTasks(): Flow<List<FinancialTask>>
    fun getTasksInDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<FinancialTask>>
    suspend fun insertTask(task: FinancialTask): Long
    suspend fun updateTask(task: FinancialTask)
    suspend fun deleteTask(task: FinancialTask)
    suspend fun updateTaskCompletionStatus(taskId: Long, isCompleted: Boolean)
} 