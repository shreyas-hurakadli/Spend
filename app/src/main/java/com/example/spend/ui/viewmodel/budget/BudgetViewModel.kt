package com.example.spend.ui.viewmodel.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.room.budget.Budget
import com.example.spend.data.room.budget.DefaultBudgetRepository
import com.example.spend.data.room.entry.DefaultRepository
import com.example.spend.ui.screen.SummaryScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val entryRepository: DefaultRepository,
    private val budgetRepository: DefaultBudgetRepository
) : ViewModel() {
    private val _budgets: MutableStateFlow<List<Pair<Budget, Double>>> =
        MutableStateFlow(value = emptyList())
    val budgets = _budgets.asStateFlow()

    private val _selectedBudget: MutableStateFlow<Pair<Budget, Double>?> =
        MutableStateFlow(value = null)
    val selectedBudget = _selectedBudget.asStateFlow()

    init {
        viewModelScope.launch {
            budgetRepository.getAllBudgets().collect { budgetList ->
                _budgets.value = budgetList.map { budget ->
                    async(context = Dispatchers.IO) {
                        val expense = when {
                            budget.accountId == 1L && budget.categoryId == 2L -> {
                                entryRepository.getExpenseByBudgetConstraintsUsingOnlyTime(
                                    startTime = budget.startTimeStamp,
                                    endTime = budget.startTimeStamp + budget.period
                                )
                            }

                            budget.accountId == 1L -> {
                                entryRepository.getExpenseByBudgetConstraintsUsingCategory(
                                    categoryId = budget.categoryId,
                                    startTime = budget.startTimeStamp,
                                    endTime = budget.startTimeStamp + budget.period
                                )
                            }

                            budget.categoryId == 2L -> {
                                entryRepository.getExpenseByBudgetConstraintsUsingAccount(
                                    accountId = budget.accountId,
                                    startTime = budget.startTimeStamp,
                                    endTime = budget.startTimeStamp + budget.period
                                )
                            }

                            else -> {
                                entryRepository.getExpenseByBudgetConstraints(
                                    accountId = budget.accountId,
                                    categoryId = budget.categoryId,
                                    startTime = budget.startTimeStamp,
                                    endTime = budget.startTimeStamp + budget.period
                                )
                            }
                        }
                        budget to expense.first()
                    }
                }.awaitAll()
            }
        }
    }

    val thereAreBudgets = budgetRepository.thereAreBudgets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = false
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedBudgetTransactions = _selectedBudget
        .flatMapLatest { budgetPair ->
            if (budgetPair == null) {
                flowOf(value = null)
            } else if (budgetPair.first.accountId == 1L && budgetPair.first.categoryId == 2L) {
                entryRepository.getEntriesByBudgetConstraintsUsingOnlyTime(
                    startTime = budgetPair.first.startTimeStamp,
                    endTime = budgetPair.first.startTimeStamp + budgetPair.first.period
                )
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
                        initialValue = emptyList()
                    )

            } else if (budgetPair.first.accountId == 1L) {
                entryRepository.getEntriesByBudgetConstraintsUsingCategory(
                    categoryId = budgetPair.first.categoryId,
                    startTime = budgetPair.first.startTimeStamp,
                    endTime = budgetPair.first.startTimeStamp + budgetPair.first.period
                )
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
                        initialValue = emptyList()
                    )
            } else if (budgetPair.first.categoryId == 2L) {
                entryRepository.getEntriesByBudgetConstraintsUsingAccount(
                    accountId = budgetPair.first.accountId,
                    startTime = budgetPair.first.startTimeStamp,
                    endTime = budgetPair.first.startTimeStamp + budgetPair.first.period,
                )
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
                        initialValue = emptyList()
                    )
            } else {
                entryRepository.getEntriesByBudgetConstraints(
                    accountId = budgetPair.first.accountId,
                    categoryId = budgetPair.first.categoryId,
                    startTime = budgetPair.first.startTimeStamp,
                    endTime = budgetPair.first.startTimeStamp + budgetPair.first.period,
                )
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
                        initialValue = emptyList()
                    )
            }
        }

    fun selectBudget(budgetPair: Pair<Budget, Double>) {
        _selectedBudget.value = budgetPair
    }
}