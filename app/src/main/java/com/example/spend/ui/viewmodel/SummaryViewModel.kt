package com.example.spend.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.getMonthStart
import com.example.spend.getSunday
import com.example.spend.getTodayStart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DURATION_MILLIS = 1_000L

data class ExpenseUiState(
    val income: Double = 0.00,
    val expense: Double = 0.00
)

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val defaultPreferencesRepository: PreferencesRepository
) : ViewModel() {
    var uiState = MutableStateFlow(ExpenseUiState())
        private set

    var selectedIndex = MutableStateFlow(0)
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    val expenseSlices = selectedIndex
        .flatMapLatest {
            when (it) {
                0 -> getExpenseByCategory(from = getTodayStart())
                1 -> getExpenseByCategory(from = getSunday())
                2 -> getExpenseByCategory(from = getMonthStart())
                else -> getExpenseByCategory(from = getMonthStart())
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val incomeSlices = selectedIndex
        .flatMapLatest {
            when (it) {
                0 -> getIncomeByCategory(from = getTodayStart())
                1 -> getIncomeByCategory(from = getSunday())
                2 -> getIncomeByCategory(from = getMonthStart())
                else -> getIncomeByCategory(from = getMonthStart())
            }
        }

    init {
        updateIndex(0)
    }

    val currencySymbol = defaultPreferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = ""
        )

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
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
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
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
                initialValue = emptyList()
            )

    fun getIncomeByTime() = defaultRepository.getIncome(
        from = when (selectedIndex.value) {
            0 -> getTodayStart()
            1 -> getSunday()
            2 -> getMonthStart()
            else -> System.currentTimeMillis() / 1000
        },
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = 0.00
        )

    fun getExpensesByTime() = defaultRepository.getExpense(
        from = when (selectedIndex.value) {
            0 -> getTodayStart()
            1 -> getSunday()
            2 -> getMonthStart()
            else -> System.currentTimeMillis() / 1000
        },
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = 0.00
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
                income = defaultRepository.getIncome(from = getMonthStart()).first(),
                expense = defaultRepository.getExpense(from = getMonthStart()).first()
            )
        }
    }

    private fun updateDailyInfo() {
        viewModelScope.launch {
            uiState.value = ExpenseUiState(
                income = defaultRepository.getIncome(from = getTodayStart()).first(),
                expense = defaultRepository.getExpense(from = getTodayStart()).first()
            )
        }
    }

    private fun updateWeekInfo() {
        viewModelScope.launch {
            uiState.value = ExpenseUiState(
                income = defaultRepository.getIncome(from = getSunday()).first(),
                expense = defaultRepository.getExpense(from = getSunday()).first()
            )
        }
    }
}