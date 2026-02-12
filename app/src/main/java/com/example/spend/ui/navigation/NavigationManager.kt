package com.example.spend.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.spend.ui.screen.AddScreen
import com.example.spend.ui.screen.CreateCategoryScreen
import com.example.spend.ui.screen.HomeScreen
import com.example.spend.ui.screen.SettingsScreen
import com.example.spend.ui.screen.SummaryScreen
import com.example.spend.ui.screen.account.AccountDetailScreen
import com.example.spend.ui.screen.account.AccountScreen
import com.example.spend.ui.screen.account.AddAccountScreen
import com.example.spend.ui.screen.budget.AddBudgetScreen
import com.example.spend.ui.screen.budget.BudgetDetailScreen
import com.example.spend.ui.screen.budget.BudgetScreen
import com.example.spend.ui.screen.entry.EntryDetailScreen
import com.example.spend.ui.screen.entry.EntryScreen

private const val durationMillis = 150

@Composable
fun NavigationManager(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = Routes.HomeScreen,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis)
            )
        }
    ) {
        composable<Routes.HomeScreen> {
            HomeScreen(navHostController = navHostController)
        }
        composable<Routes.ExpensesScreen> {
            SummaryScreen(navHostController = navHostController)
        }
        composable<Routes.AddScreen> {
            AddScreen(navHostController = navHostController)
        }
        composable<Routes.EntryScreen> {
            EntryScreen(
                navHostController = navHostController,
                viewModel = hiltViewModel(viewModelStoreOwner = it)
            )
        }
        composable<Routes.SettingsScreen> {
            SettingsScreen(navHostController = navHostController)
        }
        composable<Routes.AccountScreen> {
            AccountScreen(navHostController = navHostController)
        }
        composable<Routes.AddAccountScreen> {
            AddAccountScreen(navHostController = navHostController)
        }
        composable<Routes.CreateCategoryScreen> {
            CreateCategoryScreen(navHostController = navHostController)
        }
        composable<Routes.BudgetScreen> {
            BudgetScreen(navHostController = navHostController)
        }
        composable<Routes.AddBudgetScreen> {
            AddBudgetScreen(navHostController = navHostController)
        }
        composable<Routes.EntryDetailScreen> {
            EntryDetailScreen(
                navHostController = navHostController,
                viewModel = if (navHostController.previousBackStackEntry != null) hiltViewModel(
                    viewModelStoreOwner = navHostController.previousBackStackEntry!!
                ) else hiltViewModel()
            )
        }
        composable<Routes.BudgetDetailScreen> {
            BudgetDetailScreen(
                navHostController = navHostController,
                viewModel = if (navHostController.previousBackStackEntry != null) hiltViewModel(
                    viewModelStoreOwner = navHostController.previousBackStackEntry!!
                ) else hiltViewModel()
            )
        }
        composable<Routes.AccountDetailScreen> {
            AccountDetailScreen(
                navHostController = navHostController,
                viewModel = if (navHostController.previousBackStackEntry != null) hiltViewModel(
                    viewModelStoreOwner = navHostController.previousBackStackEntry!!
                ) else hiltViewModel()
            )
        }
    }
}