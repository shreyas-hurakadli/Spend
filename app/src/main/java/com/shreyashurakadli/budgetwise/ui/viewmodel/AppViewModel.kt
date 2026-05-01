package com.shreyashurakadli.budgetwise.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shreyashurakadli.budgetwise.data.datastore.config.PreferencesRepository
import com.shreyashurakadli.budgetwise.ui.navigation.Routes
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
            if (currency.isEmpty()) Routes.IntroductionScreen
            else Routes.HomeScreen
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = null
        )
}