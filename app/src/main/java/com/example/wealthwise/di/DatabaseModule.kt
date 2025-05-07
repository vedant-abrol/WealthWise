package com.example.wealthwise.di

import android.content.Context
import androidx.room.Room
import com.example.wealthwise.data.database.AppDatabase
import com.example.wealthwise.data.database.dao.FinancialTaskDao
import com.example.wealthwise.data.database.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideFinancialTaskDao(appDatabase: AppDatabase): FinancialTaskDao {
        return appDatabase.financialTaskDao()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(appDatabase: AppDatabase): TransactionDao {
        return appDatabase.transactionDao()
    }
} 