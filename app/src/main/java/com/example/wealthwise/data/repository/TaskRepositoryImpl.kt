package com.example.wealthwise.data.repository

import com.example.wealthwise.data.database.dao.FinancialTaskDao
import com.example.wealthwise.data.database.model.FinancialTaskEntity
import com.example.wealthwise.domain.model.FinancialTask
import com.example.wealthwise.domain.model.RecurringPeriod
import com.example.wealthwise.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val financialTaskDao: FinancialTaskDao
) : TaskRepository {

    // Mapper functions
    private fun FinancialTaskEntity.toDomain(): FinancialTask {
        return FinancialTask(
            id = this.id,
            title = this.title,
            description = this.description,
            amount = this.amount,
            dueDate = this.dueDate,
            category = this.category,
            isRecurring = this.isRecurring,
            recurringPeriod = this.recurringPeriod,
            isCompleted = this.isCompleted,
            createdAt = this.createdAt
        )
    }

    private fun FinancialTask.toEntity(): FinancialTaskEntity {
        return FinancialTaskEntity(
            id = this.id,
            title = this.title,
            description = this.description,
            amount = this.amount,
            dueDate = this.dueDate,
            category = this.category,
            isRecurring = this.isRecurring,
            recurringPeriod = this.recurringPeriod,
            isCompleted = this.isCompleted,
            createdAt = this.createdAt
        )
    }

    override fun getAllTasks(): Flow<List<FinancialTask>> {
        return financialTaskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTasksInDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<FinancialTask>> {
        return financialTaskDao.getTasksInDateRange(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertTask(task: FinancialTask): Long {
        return financialTaskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: FinancialTask) {
        financialTaskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(task: FinancialTask) {
        financialTaskDao.deleteTask(task.toEntity())
    }

    override suspend fun updateTaskCompletionStatus(taskId: Long, isCompleted: Boolean) {
        financialTaskDao.updateTaskCompletionStatus(taskId, isCompleted)
    }
} 