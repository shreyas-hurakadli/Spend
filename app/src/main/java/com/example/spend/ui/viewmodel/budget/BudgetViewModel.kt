package com.example.spend.ui.viewmodel.budget

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.room.budget.Budget
import com.example.spend.data.room.budget.DefaultBudgetRepository
import com.example.spend.data.room.entry.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.emptyList
import kotlin.math.exp

private const val TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val entryRepository: DefaultRepository,
    private val budgetRepository: DefaultBudgetRepository
) : ViewModel() {
    private val _budgets: MutableStateFlow<List<Pair<Budget, Double>>> = MutableStateFlow(emptyList())
    val budgets = _budgets.asStateFlow()

    init {
        viewModelScope.launch {
            budgetRepository.getAllBudgets().collect { budgetList ->
                _budgets.value = budgetList.map { budget ->
                    async(context = Dispatchers.IO) {
                        val expense = when {
                            budget.accountId == 1L && budget.categoryId == 2L -> {
                                Log.d("BudgetViewModel", budget.toString())
                                entryRepository.getExpenseByBudgetConstraintsUsingOnlyTime(
                                    startTime = budget.startTimeStamp,
                                    endTime = budget.startTimeStamp + budget.period
                                )
                            }
                            budget.accountId == 1L -> {
                                Log.d("BudgetViewModel", "2")
                                entryRepository.getExpenseByBudgetConstraintsUsingAccount(
                                    accountId = budget.accountId,
                                    startTime = budget.startTimeStamp,
                                    endTime = budget.startTimeStamp + budget.period
                                )
                            }
                            budget.categoryId == 2L -> {
                                Log.d("BudgetViewModel", "3")
                                entryRepository.getExpenseByBudgetConstraintsUsingCategory(
                                    categoryId = budget.categoryId,
                                    startTime = budget.startTimeStamp,
                                    endTime = budget.startTimeStamp + budget.period
                                )
                            }
                            else -> {
                                Log.d("BudgetViewModel", "4")
                                entryRepository.getExpenseByBudgetConstraints(
                                    accountId = budget.accountId,
                                    categoryId = budget.categoryId,
                                    startTime = budget.startTimeStamp,
                                    endTime = budget.startTimeStamp + budget.period
                                )
                            }
                        }
                        Log.d("BudgetViewModel", expense.first().toString())
                        budget to expense.first()
                    }
                }.awaitAll()
            }
        }
    }

    val thereAreBudgets = budgetRepository.thereAreBudgets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = false
        )
}