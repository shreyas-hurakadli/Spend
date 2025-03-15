package com.example.spend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.BalanceRepository
import com.example.spend.data.room.Entry
import com.example.spend.data.room.EntryRepository
import com.example.spend.getMonthStart
import com.example.spend.getSunday
import com.example.spend.getTodayStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ExpenseUiState(
    val balance: Int = 0,
    val expense: Int = 0
)

class ExpenseViewModel(
    private val defaultRepository: EntryRepository,
    private val dataStoreRepository: BalanceRepository
): ViewModel() {
    var uiState = MutableStateFlow(ExpenseUiState())
        private set

    var selectedIndex = MutableStateFlow(0)
        private set

    var list: StateFlow<List<Entry>> = MutableStateFlow(listOf())
        private set

    init {
        /*viewModelScope.launch {
            getList()
        }

         */
        updateIndex(0)
    }

    /*private fun getList() {
        list = defaultRepository.getTagList()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = emptyList()
            )
    }

     */

    fun updateIndex(index: Int) {
        selectedIndex.value = index
        when (selectedIndex.value) {
            0 -> { updateDailyInfo() }
            1 -> { updateWeekInfo() }
            2 -> { updateMonthlyInfo() }
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