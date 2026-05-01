package com.shreyashurakadli.budgetwise.ui.viewmodel.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.shreyashurakadli.budgetwise.data.datastore.config.PreferencesRepository
import com.shreyashurakadli.budgetwise.data.room.account.AccountRepository
import com.shreyashurakadli.budgetwise.data.room.category.CategoryRepository
import com.shreyashurakadli.budgetwise.data.room.entry.EntryRepository
import com.shreyashurakadli.budgetwise.domain.entry.DeleteTransaction
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
class EntryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val entryRepository: EntryRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val preferencesRepository: PreferencesRepository,
    private val deleteTransactionUseCase: DeleteTransaction
): ViewModel() {
    val id = savedStateHandle.toRoute<Routes.EntryDetailScreen>().id

    val entry = entryRepository.getEntryCategoryById(id = id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = null
        )

    val currencySymbol = preferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = ""
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val account = entry
        .flatMapLatest { entryCategory ->
            if (entryCategory == null) {
                emptyFlow()
            } else {
                accountRepository.getAccountById(id = entryCategory.entry.accountId)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = null
        )

    fun deleteTransaction() {
        entry.value?.let { entryCategory ->
            viewModelScope.launch {
                deleteTransactionUseCase(entry = entryCategory.entry)
            }
        }
    }
}