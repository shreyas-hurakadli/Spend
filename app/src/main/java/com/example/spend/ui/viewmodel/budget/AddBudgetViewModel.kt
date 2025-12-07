package com.example.spend.ui.viewmodel.budget

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.DefaultAccountRepository
import com.example.spend.data.room.budget.Budget
import com.example.spend.data.room.budget.DefaultBudgetRepository
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.DefaultCategoryRepository
import com.example.spend.getTodayStart
import com.example.spend.validateCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

private const val TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class AddBudgetViewModel @Inject constructor(
    private val budgetRepository: DefaultBudgetRepository,
    private val accountRepository: DefaultAccountRepository,
    private val categoryRepository: DefaultCategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(value = Budget())
    val uiState = _uiState.asStateFlow()

    private val _period = MutableStateFlow(value = Period.NONE)
    val period = _period.asStateFlow()

    private val _snackBarMessage = MutableStateFlow(value = "")
    val snackBarMessage = _snackBarMessage.asStateFlow()

    private val _showSnackBar = MutableStateFlow(value = false)
    val showSnackBar = _showSnackBar.asStateFlow()

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

    private val _selectedAccount: MutableStateFlow<Account?> = MutableStateFlow(value = null)
    val selectedAccount = _selectedAccount.asStateFlow()

    private val _selectedCategory: MutableStateFlow<Category?> = MutableStateFlow(value = null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _fromDate: MutableStateFlow<Long?> = MutableStateFlow(value = null)
    val fromDate = _fromDate.asStateFlow()

    private val _toDate: MutableStateFlow<Long?> = MutableStateFlow(value = null)
    val toDate = _toDate.asStateFlow()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                launch {
                    accountRepository.getFirstAccount()
                        .collect {
                            _selectedAccount.value = it
                            setAccount(it)
                        }
                }
                launch {
                    categoryRepository.findCategoryByNameAndId(
                        name = "All",
                        isExpense = true
                    )
                        .collect {
                            _selectedCategory.value = it
                            setCategory(it)
                        }
                }
            }
        }
    }

    fun setFromDate(date: Long) {
        _fromDate.value = date
    }

    fun setToDate(date: Long) {
        _toDate.value = date
    }

    fun setName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun setPeriod(period: Period, time: Long) {
        _period.value = period
        _uiState.value =
            _uiState.value.copy(period = if (period != Period.ONE_TIME) period.time else time)
    }

    fun setAmount(amount: String) {
        if (amount == "") {
            _uiState.value = _uiState.value.copy(amount = 0.00)
        } else if (validateCurrency(amount)) {
            _uiState.value = _uiState.value.copy(amount = amount.toDouble())
        }
    }

    fun setAccount(account: Account) {
        _uiState.value = _uiState.value.copy(accountId = account.id)
    }

    fun setCategory(category: Category) {
        _uiState.value = _uiState.value.copy(categoryId = category.id)
    }

    fun setSelectedAccount(account: Account) {
        _selectedAccount.value = account
    }

    fun setSelectedCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun clear() {
        _period.value = Period.NONE
        _uiState.value = Budget()
        viewModelScope.launch {
            withContext(context = Dispatchers.IO) {
                launch {
                    accountRepository.getFirstAccount()
                        .collect {
                            _selectedAccount.value = it
                            setAccount(it)
                        }
                }
                launch {
                    categoryRepository.findCategoryByNameAndId(
                        name = "All",
                        isExpense = true
                    )
                        .collect {
                            _selectedCategory.value = it
                            setCategory(it)
                        }
                }
            }
        }
    }

    fun toggleShowSnackBar() {
        viewModelScope.launch {
            _showSnackBar.value = !(_showSnackBar.value)
        }
    }

    private fun validateInput(): Boolean {
        return if (uiState.value.name.trim() == "") false
        else if (uiState.value.amount <= 0.00) false
        else if (uiState.value.period <= 0L) false
        else true
    }

    fun save() {
        viewModelScope.launch {
            withContext(context = Dispatchers.IO) {
                try {
                    _uiState.value = _uiState.value.copy(startTimeStamp = System.currentTimeMillis() / 1000L)
                    if (uiState.value.period == 0L) {
                        if (_toDate.value != null && _fromDate.value != null) {
                            _uiState.value = _uiState.value.copy(
                                startTimeStamp = (_fromDate.value!! / 1000L)
                            )
                            setPeriod(Period.ONE_TIME, (_toDate.value!! - _fromDate.value!!) / 1000L)
                        } else {
                            _snackBarMessage.value = "Invalid date range"
                            _showSnackBar.value = true
                        }
                    }
                    if (validateInput()) {
                        budgetRepository.insert(budget = _uiState.value)
                        clear()
                        _snackBarMessage.value = "Successful insertion"
                        _showSnackBar.value = true
                    } else {
                        _snackBarMessage.value = "Invalid input"
                        _showSnackBar.value = true
                    }
                } catch (e: SQLiteConstraintException) {
                    _snackBarMessage.value = "Budget with same name exists"
                    _showSnackBar.value = true
                } catch (e: Exception) {
                    _snackBarMessage.value = "Unexpected error occurred"
                    _showSnackBar.value = true
                }
            }
        }
    }
}

enum class Period(val time: Long) {
    NONE(time = 0L),
    ONE_TIME(time = 0L),
    WEEK(time = 604800L),
    MONTH(time = 2678400L),
    YEAR(time = 31536000L);

    override fun toString(): String {
        return when (this) {
            NONE -> "None"
            ONE_TIME -> "One Time"
            WEEK -> "Week"
            MONTH -> "Month"
            YEAR -> "Year"
        }
    }
}