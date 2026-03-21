package com.example.spend.ui.viewmodel.entry

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.data.workmanager.budget.BudgetNotificationRepository
import com.example.spend.getTodayStart
import com.example.spend.toTwoDecimal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class AddViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val defaultAccountRepository: AccountRepository,
    private val defaultCategoryRepository: CategoryRepository,
    private val defaultBudgetNotificationRepository: BudgetNotificationRepository,
    private val defaultPreferencesRepository: PreferencesRepository
) : ViewModel() {
    var selectedIndex by mutableIntStateOf(1)
        private set

    var amount by mutableStateOf("0")
        private set

    var answer by mutableDoubleStateOf(0.00)
        private set

    var operator by mutableStateOf("")
        private set

    private var operation: ((Double, Double) -> String)? by mutableStateOf(value = null)

    var date by mutableLongStateOf(value = getTodayStart())
        private set

    var time by mutableLongStateOf(value = (System.currentTimeMillis() / 1000L) - getTodayStart())
        private set

    var description by mutableStateOf("")
        private set

    var fromAccount by mutableStateOf(Account())
        private set

    var toAccount by mutableStateOf(Account())
        private set

    var category by mutableStateOf(Category())
        private set

    private val _is24hr = MutableStateFlow(value = false)
    val is24hr = _is24hr.asStateFlow()

    private var allAccount = Account()

    val accounts = defaultAccountRepository
        .getAllAccounts()
        .map { list -> list.filter { it.name != "All" } }
        .flowOn(context = Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    val incomeCategories = defaultCategoryRepository
        .getAllIncomeCategories()
        .map { list -> list.filter { it.name != "All" && it.name != "Transfer" } }
        .flowOn(context = Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    val currencySymbol = defaultPreferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = ""
        )

    val expenseCategories = defaultCategoryRepository
        .getAllExpenseCategories()
        .map { list -> list.filter { it.name != "All" && it.name != "Transfer" } }
        .flowOn(context = Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    val transferCategory = defaultCategoryRepository
        .findCategoryByNameAndId(name = "Transfer", isExpense = true)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = Category()
        )

    val transferCategoryIncome = defaultCategoryRepository
        .findCategoryByNameAndId(name = "Transfer", isExpense = false)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = Category()
        )

    private val _showSnackBar = MutableStateFlow(value = false)
    val showSnackBar = _showSnackBar.asStateFlow()

    private val _snackBarMessage = MutableStateFlow(value = "")
    val snackBarMessage = _snackBarMessage.asStateFlow()

    init {
        viewModelScope.launch {
            _is24hr.value = defaultPreferencesRepository.timeFormat.first() == "24h"
            allAccount = defaultAccountRepository.getFirstAccount().first()
        }
    }

    fun changeFromAccount(value: Account) {
        fromAccount = value
    }

    fun changeToAccount(value: Account) {
        toAccount = value
    }

    fun changeCategoryId(value: Category) {
        category = value
    }

    fun toggleShowSnackBar() {
        viewModelScope.launch {
            _showSnackBar.value = !(_showSnackBar.value)
        }
    }

    fun resetIds() {
        fromAccount = Account()
        toAccount = Account()
        category = Category()
    }

    fun changeTime(value: Long) {
        time = value
    }

    fun changeDate(value: Long) {
        date = value
    }

    fun changeSelectedIndex(index: Int) {
        selectedIndex = index
    }

    fun changeDescription(text: String) {
        description = text
    }

    fun changeAmount(value: String) {
        if (value.count { it == '.' } > 1)
            return

        val index = value.indexOf('.')
        val decimalPoints = if (index == -1) 0
        else value.length - index - 1

        if (decimalPoints > 2)
            return

        amount = String.format("%.${decimalPoints}f", value.toDouble())

        if (value.last() == '.')
            amount += '.'
    }

    fun resetOperator() {
        operator = ""
        answer = 0.00
        amount = "0"
    }

    fun changeOperator(value: String) {
        operator = value
        answer = amount.toDouble()
        amount = "0"
        operation = { a, b ->
            when (operator) {
                "+" -> (a + b).toTwoDecimal().toString()
                "-" -> (a - b).toTwoDecimal().toString()
                "÷" -> (a / b).toTwoDecimal().toString()
                "x" -> (a * b).toTwoDecimal().toString()
                else -> amount
            }
        }
    }

    fun calculateAnswer() {
        changeAmount(value = operation?.invoke(answer, amount.toDouble()) ?: amount)
    }

    private fun validateInput(): Boolean {
        if (amount == "Infinity" || amount.toDouble() <= 0.00) return false
        if (fromAccount == toAccount) return false
        if (selectedIndex != 2 && category == Category()) return false
        return true
    }

    private fun clear() {
        selectedIndex = 1;
        operation = null
        date = getTodayStart()
        time = (System.currentTimeMillis() / 1000L) - getTodayStart()
        description = ""
        resetIds()
        resetOperator()
    }

    fun save() {
        if (validateInput()) {
            viewModelScope.launch {
                try {
                    defaultRepository.insert(
                        entry = Entry(
                            amount = amount.toDouble(),
                            isExpense = (selectedIndex >= 1),
                            epochSeconds = time + date,
                            categoryId = if (selectedIndex == 2) transferCategory.value.id else category.id,
                            accountId = fromAccount.id,
                            description = description.trim()
                        )
                    )
                    if (selectedIndex > 0) {
                        defaultAccountRepository.update(
                            account = fromAccount.copy(
                                balance = fromAccount.balance - amount.toDouble()
                            )
                        )
                        if (selectedIndex == 1) {
                            defaultAccountRepository.update(
                                account = allAccount.copy(
                                    balance = allAccount.balance - amount.toDouble()
                                )
                            )
                        }
                    } else {
                        defaultAccountRepository.update(
                            account = fromAccount.copy(
                                balance = fromAccount.balance + amount.toDouble()
                            )
                        )
                        defaultAccountRepository.update(
                            account = allAccount.copy(
                                balance = allAccount.balance + amount.toDouble()
                            )
                        )
                    }

                    if (toAccount != Account()) {
                        defaultAccountRepository.update(
                            account = toAccount.copy(
                                balance = toAccount.balance + amount.toDouble()
                            )
                        )
                        defaultRepository.insert(
                            entry = Entry(
                                amount = amount.toDouble(),
                                isExpense = false,
                                epochSeconds = time + date,
                                categoryId = transferCategoryIncome.value.id,
                                accountId = toAccount.id,
                                description = description.trim()
                            )
                        )
                    }
                    if (selectedIndex >= 1) {
                        defaultBudgetNotificationRepository.checkBudgetStatus()
                    }
                    clear()
                    _snackBarMessage.value = "Successful insertion"
                    _showSnackBar.value = true
                } catch (e: SQLiteException) {
                    _snackBarMessage.value = "An entry of this name exists"
                    _showSnackBar.value = true
                } catch (e: Exception) {
                    _snackBarMessage.value = "An unknown error has occurred"
                    _showSnackBar.value = true
                }
            }
        } else {
            _snackBarMessage.value = "Specify all the fields correctly"
            _showSnackBar.value = true
        }
    }
}