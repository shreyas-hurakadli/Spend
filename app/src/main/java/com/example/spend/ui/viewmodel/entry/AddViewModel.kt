package com.example.spend.ui.viewmodel.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.workmanager.budget.BudgetNotificationRepository
import com.example.spend.domain.entry.AddEntryToDb
import com.example.spend.getTodayStart
import com.example.spend.toTwoDecimal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TIMEOUT_MILLIS = 1_000L

@HiltViewModel
class AddViewModel @Inject constructor(
    private val defaultAccountRepository: AccountRepository,
    private val defaultCategoryRepository: CategoryRepository,
    private val defaultBudgetNotificationRepository: BudgetNotificationRepository,
    private val defaultPreferencesRepository: PreferencesRepository,
    private val addEntryToDb: AddEntryToDb
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

    private val allAccount = defaultAccountRepository.getFirstAccount()

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

    private val _showToast = MutableStateFlow(value = false)
    val showToast = _showToast.asStateFlow()

    private val _toastMessage = MutableStateFlow(value = "")
    val toastMessage = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            _is24hr.value = defaultPreferencesRepository.timeFormat.first() == "24h"
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

    fun onToastShow() {
        _showToast.value = false
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
        if (amount == "Infinity" || amount.toDouble() <= 0.00) {
            showToast(message = "The amount must be greater than 0 and must not be infinity")
            return false
        } else if (fromAccount == toAccount) {
            showToast(message = "From Account should not be equal to To Account")
            return false
        } else if (selectedIndex != 2 && category == Category()) {
            showToast(message = "Category must be specified")
            return false
        }
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
            val entry = Entry(
                amount = amount.toDouble(),
                isExpense = (selectedIndex >= 1),
                epochSeconds = time + date,
                categoryId = if (selectedIndex == 2) transferCategory.value.id else category.id,
                accountId = fromAccount.id,
                description = description.trim()
            )
            viewModelScope.launch {
                val result = addEntryToDb(
                    entry = entry,
                    fromAccount = fromAccount,
                    toAccount = toAccount,
                    allAccount = allAccount.first(),
                    transferIncomeId = transferCategoryIncome.value.id,
                    selectedIndex = selectedIndex
                )
                if (result) {
                    if (selectedIndex >= 1) {
                        defaultBudgetNotificationRepository.checkBudgetStatus()
                    }
                    clear()
                    showToast(message = "Transaction is successfully added")
                } else {
                    showToast(message = "Transaction could not be added")
                }
            }
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
    }
}