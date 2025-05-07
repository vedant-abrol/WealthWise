package com.example.wealthwise.data.repository

import com.example.wealthwise.data.database.dao.TransactionDao
import com.example.wealthwise.data.database.model.TransactionEntity
import com.example.wealthwise.domain.model.Transaction
import com.example.wealthwise.domain.model.TransactionType
import com.example.wealthwise.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsInDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<Transaction>> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        return transactionDao.getTransactionsInDateRange(startDateTime, endDateTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsByCategory(category: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    override fun getBalance(): Flow<BigDecimal> {
        return transactionDao.getBalance().map { it ?: BigDecimal.ZERO }
    }

    override fun getTotalIncome(): Flow<BigDecimal> {
        return transactionDao.getTotalIncome().map { it ?: BigDecimal.ZERO }
    }

    override fun getTotalExpenses(): Flow<BigDecimal> {
        return transactionDao.getTotalExpenses().map { it ?: BigDecimal.ZERO }
    }

    private fun TransactionEntity.toDomain(): Transaction {
        return Transaction(
            id = id,
            title = title,
            description = description,
            amount = amount,
            type = type,
            category = category,
            date = date,
            createdAt = createdAt
        )
    }

    private fun Transaction.toEntity(): TransactionEntity {
        return TransactionEntity(
            id = id,
            title = title,
            description = description,
            amount = amount,
            type = type,
            category = category,
            date = date,
            createdAt = createdAt
        )
    }
} 