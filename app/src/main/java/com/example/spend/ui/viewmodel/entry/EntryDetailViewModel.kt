package com.example.spend.ui.viewmodel.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.domain.entry.DeleteTransaction
import com.example.spend.ui.navigation.Routes
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
                accountRepository.getAccountById(id = entryCategory.entry.id)
            }
        }

    fun deleteTransaction() {
        entry.value?.let { entryCategory ->
            viewModelScope.launch {
                deleteTransactionUseCase(entry = entryCategory.entry)
            }
        }
    }
}