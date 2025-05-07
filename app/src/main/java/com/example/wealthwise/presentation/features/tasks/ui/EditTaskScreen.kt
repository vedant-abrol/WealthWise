package com.example.wealthwise.presentation.features.tasks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wealthwise.domain.model.RecurringPeriod
import com.example.wealthwise.presentation.common.ErrorScreen
import com.example.wealthwise.presentation.common.LoadingIndicator
import com.example.wealthwise.presentation.features.tasks.viewmodel.EditTaskViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.error != null -> ErrorScreen(
                    message = uiState.error!!,
                    onRetry = { /* TODO: Implement retry */ }
                )
                uiState.task != null -> {
                    EditTaskContent(
                        task = uiState.task!!,
                        onUpdateTask = { title, description, amount, dueDate, category, isRecurring, recurringPeriod ->
                            viewModel.updateTask(
                                title = title,
                                description = description,
                                amount = amount,
                                dueDate = dueDate,
                                category = category,
                                isRecurring = isRecurring,
                                recurringPeriod = recurringPeriod
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskContent(
    task: com.example.wealthwise.domain.model.FinancialTask,
    onUpdateTask: (
        title: String,
        description: String?,
        amount: String,
        dueDate: LocalDate,
        category: String,
        isRecurring: Boolean,
        recurringPeriod: RecurringPeriod?
    ) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description ?: "") }
    var amount by remember { mutableStateOf(task.amount.toString()) }
    var dueDate by remember { mutableStateOf(task.dueDate) }
    var category by remember { mutableStateOf(task.category) }
    var isRecurring by remember { mutableStateOf(task.isRecurring) }
    var recurringPeriod by remember { mutableStateOf(task.recurringPeriod) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            prefix = { Text("$") }
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recurring Task")
            Switch(
                checked = isRecurring,
                onCheckedChange = { isRecurring = it }
            )
        }

        if (isRecurring) {
            RecurringPeriodSelector(
                selectedPeriod = recurringPeriod,
                onPeriodSelected = { recurringPeriod = it }
            )
        }

        Button(
            onClick = {
                onUpdateTask(
                    title = title,
                    description = description,
                    amount = amount,
                    dueDate = dueDate,
                    category = category,
                    isRecurring = isRecurring,
                    recurringPeriod = recurringPeriod
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Task")
        }
    }
} 