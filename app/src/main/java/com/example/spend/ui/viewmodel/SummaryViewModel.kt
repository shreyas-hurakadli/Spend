package com.example.spend.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.spend.data.datastore.BalanceRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.getMonthStart
import com.example.spend.getSunday
import com.example.spend.getTodayStart
import com.example.spend.longToDate
import com.example.spend.longToTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val subscriptionDuration = 5_000L

data class ExpenseUiState(
    val balance: Double = 0.00,
    val expense: Double = 0.00
)

@HiltViewModel
class SummaryViewModel @Inject constructor(
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

    fun getExpenseByCategory(from: Long, to: Long = System.currentTimeMillis() / 1000) =
        defaultRepository.getExpenseByCategory(from, to)
            .map { mp ->
                mp.map {
                    PieChartData.Slice(
                        label = it.name,
                        value = it.totalAmount.toFloat(),
                        color = Color(it.color)
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(subscriptionDuration),
                initialValue = emptyList()
            )

    fun getIncomeByCategory(from: Long, to: Long = System.currentTimeMillis() / 1000) =
        defaultRepository.getIncomeByCategory(from, to)
            .map { mp ->
                mp.map {
                    PieChartData.Slice(
                        label = it.name,
                        value = it.totalAmount.toFloat(),
                        color = Color(it.color)
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(subscriptionDuration),
                initialValue = emptyList()
            )

    fun getIncomeByTime(): StateFlow<Map<String, Double>> {
        val from: Long = when (selectedIndex.value) {
            0 -> getTodayStart()
            1 -> getSunday()
            2 -> getMonthStart()
            else -> System.currentTimeMillis() / 1000
        }
        return defaultRepository.getIncomeByTime(
            from = from,
            to = System.currentTimeMillis() / 1000
        )
            .map {
                when (selectedIndex.value) {
                    0 -> it.mapKeys { key -> longToTime(key.key) }
                    1, 2 -> it.mapKeys { key -> longToDate(key.key) }
                    else -> it.mapKeys { key -> key.key.toString() }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(subscriptionDuration),
                initialValue = emptyMap()
            )
    }

    fun getExpensesByTime(): StateFlow<Map<String, Double>> {
        val from: Long = when (selectedIndex.value) {
            0 -> getTodayStart()
            1 -> getSunday()
            2 -> getMonthStart()
            else -> System.currentTimeMillis() / 1000
        }
        return defaultRepository.getExpenseByTime(
            from = from,
            to = System.currentTimeMillis() / 1000
        )
            .map {
                when (selectedIndex.value) {
                    0 -> it.mapKeys { key -> longToTime(key.key) }
                    1, 2 -> it.mapKeys { key -> longToDate(key.key) }
                    else -> it.mapKeys { key -> longToDate(key.key) }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(subscriptionDuration),
                initialValue = emptyMap()
            )
    }

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
            withContext(context = Dispatchers.IO) {
                uiState.value = ExpenseUiState(
                    balance = defaultRepository.getIncome(getMonthStart()).first() - defaultRepository.getExpense(getMonthStart()).first(),
                    expense = defaultRepository.getExpense(getMonthStart()).first()
                )
            }
        }
    }

    private fun updateDailyInfo() {
        viewModelScope.launch {
            withContext(context = Dispatchers.IO) {
                uiState.value = ExpenseUiState(
                    balance = defaultRepository.getIncome(getTodayStart()).first() - defaultRepository.getExpense(getTodayStart()).first(),
                    expense = defaultRepository.getExpense(getTodayStart()).first()
                )
            }
        }
    }

    private fun updateWeekInfo() {
        viewModelScope.launch {
            withContext(context = Dispatchers.IO) {
                uiState.value = ExpenseUiState(
                    balance = defaultRepository.getIncome(getSunday()).first() - defaultRepository.getExpense(getSunday()).first(),
                    expense = defaultRepository.getExpense(getSunday()).first()
                )
            }
        }
    }
}