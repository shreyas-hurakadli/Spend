package com.example.spend.ui.viewmodel.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val DURATION_MILLIS = 1_000L

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    val accounts = accountRepository.getAllAccounts()
        .map { list -> list.filter { it.name != "All" } }
        .flowOn(context = Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = emptyList()
        )

    val currencySymbol = preferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = ""
        )

    val thereAreAccounts = accountRepository.thereAreAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = false
        )
}