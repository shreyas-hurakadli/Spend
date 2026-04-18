package com.example.spend.ui.viewmodel.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.budget.Budget
import com.example.spend.data.room.budget.BudgetRepository
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.domain.budget.EditBudget
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TIMEOUT_MILLIS = 1_000L

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val budgetRepository: BudgetRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val defaultPreferencesRepository: PreferencesRepository,
    private val editBudgetUseCase: EditBudget
) : ViewModel() {
    private val _budgets: MutableStateFlow<List<Pair<Budget, Double>>> =
        MutableStateFlow(value = emptyList())
    val budgets = _budgets.asStateFlow()

    val currencySymbol = defaultPreferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = ""
        )

    val currencyCode = defaultPreferencesRepository.baseCurrency
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = ""
        )

    val accounts = accountRepository.getAllAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    val categories = categoryRepository.getAllExpenseCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            budgetRepository.getAllBudgets().collect { budgetList ->
                _budgets.value = budgetList.map { budget ->
                    async {
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
}