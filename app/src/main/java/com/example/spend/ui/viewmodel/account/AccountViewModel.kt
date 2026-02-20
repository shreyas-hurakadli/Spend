package com.example.spend.ui.viewmodel.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.entry.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val DURATION_MILLIS = 1_000L

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val defaultAccountRepository: AccountRepository,
    private val defaultPreferencesRepository: PreferencesRepository
): ViewModel() {
    val accounts = defaultAccountRepository.getAllAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = emptyList()
        )

    val currencySymbol = defaultPreferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = ""
        )

    val thereAreAccounts = defaultAccountRepository.thereAreAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = false
        )

    private val _selectedAccount: MutableStateFlow<Account?> = MutableStateFlow(value = null)
    val selectedAccount = _selectedAccount.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedAccountTransactions = selectedAccount
        .flatMapLatest {
            if (it == null) {
                flowOf(value = emptyList())
            } else {
                defaultRepository.getEntriesByAccountId(it.id)
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
                        initialValue = emptyList()
                    )
            }
        }

    fun selectAccount(account: Account) {
        _selectedAccount.value = account
    }

    fun deleteAccount(account: Account?) {
        account?.let {
            viewModelScope.launch {
                withContext(context = Dispatchers.IO) {
                    defaultAccountRepository.delete(account)
                    val firstAccount = defaultAccountRepository.getFirstAccount().first()
                    defaultAccountRepository.update(firstAccount.copy(balance = firstAccount.balance - account.balance))
                }
            }
        }
    }
}