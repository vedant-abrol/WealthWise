package com.example.wealthwise.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.wealthwise.data.database.model.FinancialTaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FinancialTaskDao {
    @Query("SELECT * FROM financial_tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<FinancialTaskEntity>>

    @Query("SELECT * FROM financial_tasks WHERE dueDate BETWEEN :startDate AND :endDate")
    fun getTasksInDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<FinancialTaskEntity>>

    @Insert
    suspend fun insertTask(task: FinancialTaskEntity): Long

    @Update
    suspend fun updateTask(task: FinancialTaskEntity)

    @Delete
    suspend fun deleteTask(task: FinancialTaskEntity)

    // Added based on ViewModel example
    @Query("UPDATE financial_tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskCompletionStatus(taskId: Long, isCompleted: Boolean)
} 