package com.shreyashurakadli.budgetwise.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shreyashurakadli.budgetwise.data.datastore.config.PreferencesRepository
import com.shreyashurakadli.budgetwise.data.dto.EntryCategory
import com.shreyashurakadli.budgetwise.data.room.account.Account
import com.shreyashurakadli.budgetwise.data.room.account.AccountRepository
import com.shreyashurakadli.budgetwise.data.room.entry.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val durationMillis = 1_000L

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val defaultAccountRepository: AccountRepository,
    private val defaultPreferencesRepository: PreferencesRepository
) : ViewModel() {
    val transactions: StateFlow<List<EntryCategory>> =
        defaultRepository.getEntryIconAndColor(limit = 4)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(durationMillis),
                initialValue = emptyList()
            )
    val accountList: StateFlow<List<Account>> =
        defaultAccountRepository.getAllAccounts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(durationMillis),
                initialValue = emptyList()
            )

    val currentAccount =
        defaultAccountRepository.getFirstAccount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = Account()
            )

    val currencySymbol = defaultPreferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ""
        )
}