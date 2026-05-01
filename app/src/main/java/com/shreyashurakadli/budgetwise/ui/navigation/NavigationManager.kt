package com.shreyashurakadli.budgetwise.ui.navigation

import android.content.Intent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shreyashurakadli.budgetwise.data.intent.PendingIntentData
import com.shreyashurakadli.budgetwise.ui.screen.HomeScreen
import com.shreyashurakadli.budgetwise.ui.screen.IntroductionScreen
import com.shreyashurakadli.budgetwise.ui.screen.SettingsScreen
import com.shreyashurakadli.budgetwise.ui.screen.SummaryScreen
import com.shreyashurakadli.budgetwise.ui.screen.account.AccountDetailScreen
import com.shreyashurakadli.budgetwise.ui.screen.account.AccountScreen
import com.shreyashurakadli.budgetwise.ui.screen.account.AddAccountScreen
import com.shreyashurakadli.budgetwise.ui.screen.account.EditAccountScreen
import com.shreyashurakadli.budgetwise.ui.screen.budget.AddBudgetScreen
import com.shreyashurakadli.budgetwise.ui.screen.budget.BudgetDetailScreen
import com.shreyashurakadli.budgetwise.ui.screen.budget.BudgetScreen
import com.shreyashurakadli.budgetwise.ui.screen.budget.EditBudgetScreen
import com.shreyashurakadli.budgetwise.ui.screen.category.CategoryDetailScreen
import com.shreyashurakadli.budgetwise.ui.screen.category.CategoryScreen
import com.shreyashurakadli.budgetwise.ui.screen.category.CreateCategoryScreen
import com.shreyashurakadli.budgetwise.ui.screen.category.EditCategoryScreen
import com.shreyashurakadli.budgetwise.ui.screen.currency.CurrencyConverterScreen
import com.shreyashurakadli.budgetwise.ui.screen.currency.SelectCurrencyScreen
import com.shreyashurakadli.budgetwise.ui.screen.entry.AddScreen
import com.shreyashurakadli.budgetwise.ui.screen.entry.EditTransactionScreen
import com.shreyashurakadli.budgetwise.ui.screen.entry.EntryDetailScreen
import com.shreyashurakadli.budgetwise.ui.screen.entry.EntryScreen
import com.shreyashurakadli.budgetwise.ui.viewmodel.AppViewModel

private const val durationMillis = 150

@Composable
fun NavigationManager(
    navHostController: NavHostController,
    intent: Intent,
    viewModel: AppViewModel = hiltViewModel()
) {
    val startDestination by viewModel.startDestination.collectAsState()

    if (startDestination == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navHostController,
            startDestination = startDestination ?: Routes.IntroductionScreen,
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
            composable<Routes.IntroductionScreen> {
                IntroductionScreen(navHostController = navHostController)
            }
            composable<Routes.SelectCurrencyScreen> {
                SelectCurrencyScreen(navHostController = navHostController)
            }
            composable<Routes.HomeScreen> {
                HomeScreen(navHostController = navHostController)
            }
            composable<Routes.SummaryScreen> {
                SummaryScreen(navHostController = navHostController)
            }
            composable<Routes.AddScreen> {
                AddScreen(navHostController = navHostController)
            }
            composable<Routes.EntryScreen> {
                EntryScreen(navHostController = navHostController)
            }
            composable<Routes.SettingsScreen> {
                SettingsScreen(navHostController = navHostController)
            }
            composable<Routes.AccountScreen> {
                AccountScreen(navHostController = navHostController)
            }
            composable<Routes.CategoryScreen> {
                CategoryScreen(navHostController = navHostController)
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
            composable<Routes.CurrencyConverterScreen> {
                CurrencyConverterScreen(navHostController = navHostController)
            }
            composable<Routes.EntryDetailScreen> {
                EntryDetailScreen(navHostController = navHostController)
            }
            composable<Routes.BudgetDetailScreen> {
                BudgetDetailScreen(navHostController = navHostController)
            }
            composable<Routes.CategoryDetailScreen> {
                CategoryDetailScreen(navHostController = navHostController)
            }
            composable<Routes.AccountDetailScreen> {
                AccountDetailScreen(navHostController = navHostController)
            }
            composable<Routes.EditTransactionScreen> {
                EditTransactionScreen(navHostController = navHostController)
            }
            composable<Routes.EditAccountScreen> {
                EditAccountScreen(navHostController = navHostController)
            }
            composable<Routes.EditBudgetScreen> {
                EditBudgetScreen(navHostController = navHostController)
            }
            composable<Routes.EditCategoryScreen> {
                EditCategoryScreen(navHostController = navHostController)
            }
        }

        handleIntent(
            intent = intent,
            navHostController = navHostController
        )
    }

}

private fun handleIntent(
    intent: Intent,
    navHostController: NavHostController
) {
    val budgetId = intent.getLongExtra(PendingIntentData.budgetId, -1L)
    if (budgetId != -1L) {
        navHostController.navigate(Routes.BudgetDetailScreen(id = budgetId))
    }
}
