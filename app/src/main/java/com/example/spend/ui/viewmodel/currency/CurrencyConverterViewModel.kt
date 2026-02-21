package com.example.spend.ui.viewmodel.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.room.currency.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val DURATION_MILLIS = 1_000L

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val defaultCurrencyRepository: CurrencyRepository
): ViewModel() {
    val currencies = defaultCurrencyRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = emptyList()
        )
}