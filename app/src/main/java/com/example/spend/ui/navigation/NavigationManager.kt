package com.example.spend.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.spend.ui.screen.AccountScreen
import com.example.spend.ui.screen.AddScreen
import com.example.spend.ui.screen.EntryScreen
import com.example.spend.ui.screen.SummaryScreen
import com.example.spend.ui.screen.HomeScreen

private const val durationMillis = 150
@Composable
fun NavigationManager(navHostController: NavHostController) {
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
            EntryScreen(navHostController = navHostController)
        }
        composable<Routes.AccountScreen> {
            AccountScreen(navHostController = navHostController)
        }
    }
}