package com.example.spend.ui.viewmodel.budget

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.budget.Budget
import com.example.spend.data.room.budget.BudgetRepository
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.ui.MAX_BUDGET_NAME_LENGTH
import com.example.spend.ui.MAX_ENTRY_AMOUNT
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

private const val TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class AddBudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val defaultPreferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(value = Budget())
    val uiState = _uiState.asStateFlow()

    private val _period = MutableStateFlow(value = Period.NONE)
    val period = _period.asStateFlow()

    private val _toastMessage = MutableStateFlow(value = "")
    val toastMessage = _toastMessage.asStateFlow()

    private val _showToast = MutableStateFlow(value = false)
    val showSnackBar = _showToast.asStateFlow()

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

    val currencySymbol = defaultPreferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ""
        )

    val currencyCode = defaultPreferencesRepository.baseCurrency
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ""
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
            _showToast.value = !(_showToast.value)
        }
    }

    fun checkAmount(amount: String): Boolean =
        amount.isNotEmpty() && amount.toDouble() > 100000000000

    private fun validateInput(): Boolean {
        return if (_uiState.value.name.isBlank()) {
            showToast(message = "Name should not be blank")
            false
        } else if (_uiState.value.name.length > MAX_BUDGET_NAME_LENGTH) {
            showToast(message = "Length of the name should not exceed $MAX_BUDGET_NAME_LENGTH")
            false
        } else if (_uiState.value.amount > MAX_ENTRY_AMOUNT) {
            showToast(message = "Amount must not exceed $MAX_ENTRY_AMOUNT")
            false
        } else if (_uiState.value.amount <= 0.00) {
            showToast(message = "Amount should not be negative")
            false
        } else if (_uiState.value.period <= 0L) {
            showToast(message = "Start date must be before end date")
            false
        } else {
            true
        }
    }

    fun save() {
        _uiState.value =
            _uiState.value.copy(startTimeStamp = System.currentTimeMillis() / 1000L)
        if (uiState.value.period == 0L) {
            _fromDate.value?.let { fromDate ->
                _uiState.value = _uiState.value.copy(
                    startTimeStamp = (fromDate / 1000L)
                )
                _toDate.value?.let { toDate ->
                    setPeriod(Period.ONE_TIME, time = (toDate - fromDate) / 1000L)
                }
            }
            if (_fromDate.value == null || _toDate.value == null) {
                showToast(message = "Specify a period")
                return
            }
        }
        viewModelScope.launch {
            try {
                if (validateInput()) {
                    budgetRepository.insert(budget = _uiState.value)
                    clear()
                    showToast(message = "Successfully created a budget")
                }
            } catch (e: SQLiteConstraintException) {
                showToast(message = "Budget with same name exists")
            } catch (e: Exception) {
                showToast(message = "An unknown error has occurred")
            }
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
    }

    fun onToastShow() {
        _showToast.value = false
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