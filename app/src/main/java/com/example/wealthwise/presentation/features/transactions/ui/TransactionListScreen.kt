package com.example.wealthwise.presentation.features.transactions.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wealthwise.R
import com.example.wealthwise.domain.model.Transaction
import com.example.wealthwise.domain.model.TransactionType
import com.example.wealthwise.presentation.features.transactions.viewmodel.TransactionListUiState
import com.example.wealthwise.presentation.features.transactions.viewmodel.TransactionListViewModel
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.filled.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onAddTransaction: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.transactions)) },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.filter))
                    }
                    IconButton(onClick = onAddTransaction) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_transaction))
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                FinancialSummaryCard(uiState)
                TransactionList(
                    transactions = uiState.transactions,
                    onTransactionClick = onTransactionClick,
                    onDeleteTransaction = { viewModel.deleteTransaction(it) }
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }

        if (showFilterMenu) {
            FilterMenu(
                uiState.selectedType,
                uiState.selectedCategory,
                { viewModel.setTransactionType(it) },
                { viewModel.setCategory(it) },
                { showDateRangePicker = true },
                { viewModel.clearFilters() },
                { showFilterMenu = false }
            )
        }

        if (showDateRangePicker) {
            DateRangePickerDialog(
                { start, end ->
                    viewModel.setDateRange(start, end)
                    showDateRangePicker = false
                },
                { showDateRangePicker = false }
            )
        }
    }
}

@Composable
fun FinancialSummaryCard(uiState: TransactionListUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.current_balance),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$${uiState.balance}",
                style = MaterialTheme.typography.headlineMedium,
                color = if (uiState.balance >= BigDecimal.ZERO) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.total_income),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$${uiState.totalIncome}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                        text = stringResource(R.string.total_expenses),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$${uiState.totalExpenses}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionList(
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {
    if (transactions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_transactions),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction) },
                    onDelete = { onDeleteTransaction(transaction) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = transaction.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${transaction.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (transaction.type == TransactionType.INCOME)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.delete_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun FilterMenu(
    selectedType: TransactionType?,
    selectedCategory: String?,
    onTypeSelected: (TransactionType?) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onDateRangeSelected: () -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.filter)) },
        text = {
            Column {
                Text(stringResource(R.string.transaction_type), style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TransactionType.values().forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { onTypeSelected(type) },
                            label = { Text(type.name.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.select_date_range), style = MaterialTheme.typography.titleSmall)
                Button(
                    onClick = onDateRangeSelected,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.select_date_range))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onClearFilters,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.clear_filters))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
fun DateRangePickerDialog(
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_date_range)) },
        text = {
            Column {
                // TODO: Implement proper date picker
                Text("${stringResource(R.string.start_date)}: ${startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}")
                Text("${stringResource(R.string.end_date)}: ${endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onDateRangeSelected(startDate, endDate) }
            ) {
                Text(stringResource(R.string.apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
} 