package com.example.wealthwise.presentation.features.transactions.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wealthwise.R
import com.example.wealthwise.domain.model.Transaction
import com.example.wealthwise.domain.model.TransactionType
import com.example.wealthwise.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

data class AddTransactionUiState(
    val title: String = "",
    val description: String = "",
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = "",
    val date: LocalDate = LocalDate.now(),
    val titleError: String? = null,
    val amountError: String? = null,
    val categoryError: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState

    fun setTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            titleError = if (title.isBlank()) context.getString(R.string.required_field) else null
        )
    }

    fun setDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun setAmount(amount: String) {
        _uiState.value = _uiState.value.copy(
            amount = amount,
            amountError = validateAmount(amount)
        )
    }

    fun setType(type: TransactionType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun setCategory(category: String) {
        _uiState.value = _uiState.value.copy(
            category = category,
            categoryError = if (category.isBlank()) context.getString(R.string.required_field) else null
        )
    }

    fun setDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun saveTransaction() {
        val currentState = _uiState.value

        // Validate all fields
        val titleError = if (currentState.title.isBlank()) context.getString(R.string.required_field) else null
        val amountError = validateAmount(currentState.amount)
        val categoryError = if (currentState.category.isBlank()) context.getString(R.string.required_field) else null

        if (titleError != null || amountError != null || categoryError != null) {
            _uiState.value = currentState.copy(
                titleError = titleError,
                amountError = amountError,
                categoryError = categoryError
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isLoading = true)

                val transaction = Transaction(
                    id = 0, // Room will generate the ID
                    title = currentState.title,
                    description = currentState.description,
                    amount = BigDecimal(currentState.amount),
                    type = currentState.type,
                    category = currentState.category,
                    date = currentState.date.atStartOfDay(),
                    createdAt = LocalDateTime.now()
                )

                transactionRepository.insertTransaction(transaction)
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    amountError = context.getString(R.string.save_error, e.message)
                )
            }
        }
    }

    private fun validateAmount(amount: String): String? {
        if (amount.isBlank()) {
            return context.getString(R.string.required_field)
        }
        return try {
            val value = BigDecimal(amount)
            if (value <= BigDecimal.ZERO) {
                context.getString(R.string.amount_positive)
            } else {
                null
            }
        } catch (e: NumberFormatException) {
            context.getString(R.string.invalid_amount)
        }
    }
} 