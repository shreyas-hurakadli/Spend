package com.example.spend.ui.viewmodel.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.di.module.domain.EditAccount
import com.example.spend.domain.account.DeleteAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val DURATION_MILLIS = 1_000L

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val defaultAccountRepository: AccountRepository,
    private val defaultPreferencesRepository: PreferencesRepository,
    private val deleteAccountUseCase: DeleteAccount,
    private val editAccountUseCase: EditAccount
) : ViewModel() {
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

    private val _showToast = MutableStateFlow(value = false)
    val showToast = _showToast.asStateFlow()

    private val _toastMessage = MutableStateFlow(value = "")
    val toastMessage = _toastMessage.asStateFlow()

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
                val result = deleteAccountUseCase(account = it)
                if (result) {
                    showToast(message = "Account is successfully deleted")
                } else {
                    showToast(message = "Account could not be deleted")
                }
            }
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
    }

    fun onToastShown() {
        _showToast.value = false
    }

    fun editAccount(editedAccount: Account) {
        viewModelScope.launch {
            val result = editAccountUseCase(
                account = _selectedAccount.value ?: Account(),
                editedAccount = editedAccount,
            )
            if (result) {
                showToast(message = "Account is successfully edited")
                _selectedAccount.value = editedAccount
            } else {
                showToast(message = "Account could not be edited")
            }
        }
    }
}