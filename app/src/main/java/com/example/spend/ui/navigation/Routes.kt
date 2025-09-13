package com.example.spend.ui.navigation

import kotlinx.serialization.Serializable

sealed class Routes {
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
    data object AddAccountScreen: Routes()
}