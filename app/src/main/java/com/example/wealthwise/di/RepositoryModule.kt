package com.example.wealthwise.di

import com.example.wealthwise.data.repository.TaskRepositoryImpl
import com.example.wealthwise.data.repository.TransactionRepositoryImpl
import com.example.wealthwise.domain.repository.TaskRepository
import com.example.wealthwise.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(taskRepositoryImpl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(transactionRepositoryImpl: TransactionRepositoryImpl): TransactionRepository
} 