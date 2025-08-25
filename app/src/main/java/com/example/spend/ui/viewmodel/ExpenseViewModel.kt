package com.example.spend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.BalanceRepository
import com.example.spend.data.room.EntryRepository
import com.example.spend.getMonthStart
import com.example.spend.getSunday
import com.example.spend.getTodayStart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val subscriptionDuration = 5_000L

data class ExpenseUiState(
    val balance: Double = 0.00,
    val expense: Double = 0.00
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val dataStoreRepository: BalanceRepository
) : ViewModel() {
    var uiState = MutableStateFlow(ExpenseUiState())
        private set

    var selectedIndex = MutableStateFlow(0)
        private set

    init {
        updateIndex(0)
    }

    fun getExpenseByCategory() = defaultRepository.getExpenseByCategory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(subscriptionDuration),
            initialValue = emptyMap()
        )

    fun getIncomeByCategory() = defaultRepository.getIncomeByCategory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(subscriptionDuration),
            initialValue = emptyMap()
        )

    fun getAllIncomeAmount() = defaultRepository.getAllIncomeAmount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(subscriptionDuration),
            initialValue = emptyList()
        )

    fun getAllExpenseAmount() = defaultRepository.getAllExpenseAmount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(subscriptionDuration),
            initialValue = emptyList()
        )

    fun transactionsPresent() = defaultRepository.areEntriesPresent()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(subscriptionDuration),
            initialValue = false
        )

    fun updateIndex(index: Int) {
        selectedIndex.value = index
        when (selectedIndex.value) {
            0 -> {
                updateDailyInfo()
            }

            1 -> {
                updateWeekInfo()
            }

            2 -> {
                updateMonthlyInfo()
            }
        }
    }

    private fun updateMonthlyInfo() {
        viewModelScope.launch {
            uiState.value = ExpenseUiState(
                balance = dataStoreRepository.balance.first(),
                expense = defaultRepository.getExpense(getMonthStart()).first()
            )
        }
    }

    private fun updateDailyInfo() {
        viewModelScope.launch {
            uiState.value = ExpenseUiState(
                balance = dataStoreRepository.balance.first(),
                expense = defaultRepository.getExpense(getTodayStart()).first()
            )
        }
    }

    private fun updateWeekInfo() {
        viewModelScope.launch {
            uiState.value = ExpenseUiState(
                balance = dataStoreRepository.balance.first(),
                expense = defaultRepository.getExpense(getSunday()).first()
            )
        }
    }
}