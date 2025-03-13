package com.example.spend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.spend.ui.screen.AddScreen
import com.example.spend.ui.screen.EntryScreen
import com.example.spend.ui.screen.ExpensesScreen
import com.example.spend.ui.screen.HomeScreen

@Composable
fun NavigationManager(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = Routes.HomeScreen,
    ) {
        composable<Routes.HomeScreen> {
            HomeScreen(navHostController = navHostController)
        }
        composable<Routes.ExpensesScreen> {
            ExpensesScreen(navHostController = navHostController)
        }
        composable<Routes.AddScreen> {
            AddScreen(navHostController = navHostController)
        }
        composable<Routes.EntryScreen> {
            EntryScreen(navHostController = navHostController)
        }
    }
}