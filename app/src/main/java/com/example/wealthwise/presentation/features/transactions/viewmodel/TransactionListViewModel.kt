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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val balance: BigDecimal = BigDecimal.ZERO,
    val totalIncome: BigDecimal = BigDecimal.ZERO,
    val totalExpenses: BigDecimal = BigDecimal.ZERO,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDateRange: Pair<LocalDate, LocalDate>? = null,
    val selectedType: TransactionType? = null,
    val selectedCategory: String? = null
)

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val currentState = _uiState.value
            val transactionsFlow = when {
                currentState.selectedDateRange != null -> {
                    transactionRepository.getTransactionsInDateRange(
                        currentState.selectedDateRange.first,
                        currentState.selectedDateRange.second
                    )
                }
                currentState.selectedType != null -> {
                    transactionRepository.getTransactionsByType(currentState.selectedType)
                }
                currentState.selectedCategory != null -> {
                    transactionRepository.getTransactionsByCategory(currentState.selectedCategory)
                }
                else -> transactionRepository.getAllTransactions()
            }

            combine(
                transactionsFlow,
                transactionRepository.getBalance(),
                transactionRepository.getTotalIncome(),
                transactionRepository.getTotalExpenses()
            ) { transactions, balance, income, expenses ->
                _uiState.value = _uiState.value.copy(
                    transactions = transactions,
                    balance = balance,
                    totalIncome = income,
                    totalExpenses = expenses,
                    isLoading = false
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = context.getString(R.string.load_error, e.message)
                )
            }.onEach { }.launchIn(viewModelScope)
        }
    }

    fun setDateRange(startDate: LocalDate, endDate: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDateRange = Pair(startDate, endDate))
        loadTransactions()
    }

    fun setTransactionType(type: TransactionType?) {
        _uiState.value = _uiState.value.copy(
            selectedType = type,
            selectedDateRange = null,
            selectedCategory = null
        )
        loadTransactions()
    }

    fun setCategory(category: String?) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            selectedDateRange = null,
            selectedType = null
        )
        loadTransactions()
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            selectedDateRange = null,
            selectedType = null,
            selectedCategory = null
        )
        loadTransactions()
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(transaction)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = context.getString(R.string.delete_error, e.message)
                )
            }
        }
    }
} 