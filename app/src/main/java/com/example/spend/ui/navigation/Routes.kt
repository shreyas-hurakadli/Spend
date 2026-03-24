package com.example.spend.ui.navigation

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    data object IntroductionScreen: Routes()

    @Serializable
    data object SelectCurrencyScreen: Routes()

    @Serializable
    data object HomeScreen: Routes()

    @Serializable
    data object ExpensesScreen: Routes()

    @Serializable
    data object AddScreen: Routes()

    @Serializable
    data object EntryScreen: Routes()

    @Serializable
    data object SettingsScreen: Routes()

    @Serializable
    data object AccountScreen: Routes()

    @Serializable
    data object AddAccountScreen: Routes()

    @Serializable
    data object CreateCategoryScreen: Routes()

    @Serializable
    data object BudgetScreen: Routes()

    @Serializable
    data object AddBudgetScreen: Routes()

    @Serializable
    data object EntryDetailScreen: Routes()

    @Serializable
    data object BudgetDetailScreen: Routes()

    @Serializable
    data object AccountDetailScreen: Routes()

    @Serializable
    data object EditTransactionScreen: Routes()

    @Serializable
    data object EditAccountScreen: Routes()

    @Serializable
    data object EditBudgetScreen: Routes()

    @Serializable
    data object CurrencyConverterScreen: Routes()

    @Serializable
    data object CategoryScreen: Routes()
}