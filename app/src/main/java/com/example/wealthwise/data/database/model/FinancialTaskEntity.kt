package com.example.wealthwise.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wealthwise.domain.model.RecurringPeriod
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "financial_tasks")
data class FinancialTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val amount: BigDecimal,
    val dueDate: LocalDate,
    val category: String,
    val isRecurring: Boolean,
    val recurringPeriod: RecurringPeriod?,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
) 