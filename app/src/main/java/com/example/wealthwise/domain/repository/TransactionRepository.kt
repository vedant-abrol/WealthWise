package com.example.wealthwise.domain.repository

import com.example.wealthwise.domain.model.Transaction
import com.example.wealthwise.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.math.BigDecimal

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsInDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>>
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    fun getBalance(): Flow<BigDecimal>
    fun getTotalIncome(): Flow<BigDecimal>
    fun getTotalExpenses(): Flow<BigDecimal>
} 