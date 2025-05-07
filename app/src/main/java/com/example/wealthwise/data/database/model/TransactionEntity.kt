package com.example.wealthwise.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wealthwise.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val amount: BigDecimal,
    val type: TransactionType,
    val category: String,
    val date: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now()
) 