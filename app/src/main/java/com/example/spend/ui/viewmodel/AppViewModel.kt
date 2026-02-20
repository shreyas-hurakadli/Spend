package com.example.spend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val DURATION_MILLIS = 1_000L

@HiltViewModel
class AppViewModel @Inject constructor(
    private val defaultPreferencesRepository: PreferencesRepository
): ViewModel() {
    val startDestination = defaultPreferencesRepository.baseCurrency
        .map { currency ->
            if (currency.isEmpty()) Routes.CurrencyScreen
            else Routes.HomeScreen
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = null
        )
}