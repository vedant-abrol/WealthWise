package com.example.wealthwise.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.wealthwise.presentation.features.tasks.ui.AddTaskScreen
import com.example.wealthwise.presentation.features.tasks.ui.EditTaskScreen
import com.example.wealthwise.presentation.features.tasks.ui.TaskDetailsScreen
import com.example.wealthwise.presentation.features.tasks.ui.TaskListScreen
import com.example.wealthwise.presentation.features.transactions.ui.AddTransactionScreen
import com.example.wealthwise.presentation.features.transactions.ui.TransactionListScreen

sealed class Screen(val route: String) {
    object TaskList : Screen("taskList")
    object TaskDetails : Screen("taskDetails/{taskId}") {
        fun createRoute(taskId: Long) = "taskDetails/$taskId"
    }
    object AddTask : Screen("addTask")
    object EditTask : Screen("editTask/{taskId}") {
        fun createRoute(taskId: Long) = "editTask/$taskId"
    }
    object Transactions : Screen("transactions")
    object AddTransaction : Screen("addTransaction")
    object Insights : Screen("insights")
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.TaskList.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.TaskList.route) {
            TaskListScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetails.createRoute(taskId))
                },
                onAddTaskClick = {
                    navController.navigate(Screen.AddTask.route)
                }
            )
        }

        composable(
            route = Screen.TaskDetails.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
            TaskDetailsScreen(
                onNavigateBack = { navController.popBackStack() },
                onEditTask = { taskId ->
                    navController.navigate(Screen.EditTask.createRoute(taskId))
                }
            )
        }

        composable(Screen.AddTask.route) {
            AddTaskScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType }
            )
        ) {
            EditTaskScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Transactions.route) {
            TransactionListScreen(
                onAddTransaction = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onTransactionClick = { /* TODO: Implement transaction details */ }
            )
        }

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Insights.route) {
            // TODO: Implement insights screen
        }

        composable(Screen.Settings.route) {
            // TODO: Implement settings screen
        }
    }
} 