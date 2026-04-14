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
import com.example.spend.ui.data.MAX_BUDGET_NAME_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
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
import kotlin.math.max

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

    private val _selectedBudget: MutableStateFlow<Pair<Budget, Double>?> =
        MutableStateFlow(value = null)
    val selectedBudget = _selectedBudget.asStateFlow()

    private val _showToast = MutableStateFlow(value = false)
    val showToast = _showToast.asStateFlow()

    private val _toastMessage = MutableStateFlow(value = "")
    val toastMessage = _toastMessage.asStateFlow()

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

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.delete(budget = budget)
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
    }

    fun onToastShow() {
        _showToast.value = false
    }

    private fun validateEditedBudget(editedBudget: Budget): Boolean =
        if (editedBudget.name.length > MAX_BUDGET_NAME_LENGTH) {
            showToast(message = "Budget name length should be less than ${MAX_BUDGET_NAME_LENGTH + 1}")
            false
        } else if (editedBudget.period <= 0) {
            showToast(message = "Invalid period specification")
            false
        } else {
            true
        }

    fun editBudget(editedBudget: Budget) {
        if (validateEditedBudget(editedBudget)) {
            viewModelScope.launch {
                val result = editBudgetUseCase(editedBudget = editedBudget)
                if (result) {
                    _selectedBudget.value = Pair(
                        first = editedBudget,
                        second = max(a = _selectedBudget.value?.second ?: 0.00, b = 0.00)
                    )
                    showToast(message = "Successfully edited budget")
                } else {
                    showToast(message = "Failed to edit budget")
                }
            }
        } else {
            showToast(message = "Specify the fields correctly")
        }
    }
}