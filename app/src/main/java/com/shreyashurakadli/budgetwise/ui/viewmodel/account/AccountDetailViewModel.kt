package com.shreyashurakadli.budgetwise.ui.viewmodel.account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.shreyashurakadli.budgetwise.data.datastore.config.PreferencesRepository
import com.shreyashurakadli.budgetwise.data.room.account.AccountRepository
import com.shreyashurakadli.budgetwise.data.room.entry.EntryRepository
import com.shreyashurakadli.budgetwise.domain.account.DeleteAccount
import com.shreyashurakadli.budgetwise.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val durationMillis = 1_000L

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val entryRepository: EntryRepository,
    private val accountRepository: AccountRepository,
    private val preferencesRepository: PreferencesRepository,
    private val deleteAccountUseCase: DeleteAccount
) : ViewModel() {
    val id = savedStateHandle.toRoute<Routes.AccountDetailScreen>().id

    val account = accountRepository.getAccountById(id = id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = account
        .flatMapLatest { account ->
            if (account == null) {
                emptyFlow()
            } else {
                entryRepository.getEntriesByAccountId(id = account.id)
            }
        }

    val currencySymbol = preferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = ""
        )

    fun deleteAccount() {
        account.value?.let { account ->
            viewModelScope.launch {
                val result = deleteAccountUseCase(account = account)
            }
        }
    }
}