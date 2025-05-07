package com.example.wealthwise.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Domain model for a financial task.
 */
data class FinancialTask(
    val id: Long = 0,
    val title: String,
    val description: String?,
    val amount: BigDecimal,
    val dueDate: LocalDate,
    val category: String,
    val isRecurring: Boolean,
    val recurringPeriod: RecurringPeriod?,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime
) 