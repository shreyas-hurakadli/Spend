package com.example.spend.ui.viewmodel.budget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.budget.BudgetRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val TIMEOUT_MILLIS = 1_000L

@HiltViewModel
class BudgetDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val entryRepository: EntryRepository,
    private val budgetRepository: BudgetRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val budgetId = savedStateHandle.toRoute<Routes.BudgetDetailScreen>().id

    val budget = budgetRepository.getBudgetById(id = budgetId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = budget
        .flatMapLatest { budget ->
            if (budget == null) {
                flowOf(value = emptyList())
            } else if (budget.accountId == 1L && budget.categoryId == 2L) {
                entryRepository.getEntriesByBudgetConstraintsUsingOnlyTime(
                    startTime = budget.startTimeStamp,
                    endTime = budget.startTimeStamp + budget.period
                )
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
                        initialValue = emptyList()
                    )

            } else if (budget.accountId == 1L) {
                entryRepository.getEntriesByBudgetConstraintsUsingCategory(
                    categoryId = budget.categoryId,
                    startTime = budget.startTimeStamp,
                    endTime = budget.startTimeStamp + budget.period
                )
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
                        initialValue = emptyList()
                    )
            } else if (budget.categoryId == 2L) {
                entryRepository.getEntriesByBudgetConstraintsUsingAccount(
                    accountId = budget.accountId,
                    startTime = budget.startTimeStamp,
                    endTime = budget.startTimeStamp + budget.period,
                )
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
                        initialValue = emptyList()
                    )
            } else {
                entryRepository.getEntriesByBudgetConstraints(
                    accountId = budget.accountId,
                    categoryId = budget.categoryId,
                    startTime = budget.startTimeStamp,
                    endTime = budget.startTimeStamp + budget.period,
                )
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
                        initialValue = emptyList()
                    )
            }
        }

    val expenses = transactions
        .map { it.sumOf { entryCategory -> entryCategory.entry.amount } }
        .flowOn(context = Dispatchers.Default)

    val currencySymbol = preferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = ""
        )
}