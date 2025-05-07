package com.example.wealthwise.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Transaction(
    val id: Long = 0,
    val title: String,
    val description: String?,
    val amount: BigDecimal,
    val type: TransactionType,
    val category: String,
    val date: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class TransactionType {
    INCOME,
    EXPENSE
} 