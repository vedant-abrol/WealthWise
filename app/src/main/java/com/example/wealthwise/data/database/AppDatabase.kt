package com.example.wealthwise.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wealthwise.data.database.dao.FinancialTaskDao
import com.example.wealthwise.data.database.dao.TransactionDao
import com.example.wealthwise.data.database.model.FinancialTaskEntity
import com.example.wealthwise.data.database.model.TransactionEntity

@Database(
    entities = [FinancialTaskEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(com.example.wealthwise.data.database.TypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun financialTaskDao(): FinancialTaskDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val DATABASE_NAME = "wealthwise_db"
    }
} 