package com.example.spend.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.account.DefaultAccountRepository
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.DefaultCategoryRepository
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.ui.screen.AddAccountScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.ZoneId
import javax.inject.Inject

private const val TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class AddViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val defaultAccountRepository: DefaultAccountRepository,
    private val defaultCategoryRepository: DefaultCategoryRepository
) : ViewModel() {
    var selectedIndex by mutableIntStateOf(1)
        private set

    var amount by mutableStateOf("0")
        private set

    var answer by mutableDoubleStateOf(0.00)
        private set

    var operator by mutableStateOf("")
        private set

    private var operation: ((Double, Double) -> String)? by mutableStateOf(null)

    var time by mutableLongStateOf(System.currentTimeMillis())
        private set

    var description by mutableStateOf("")
        private set

    var fromAccount by mutableStateOf(Account())
        private set

    var toAccount by mutableStateOf(Account())
        private set

    var category by mutableStateOf(Category())
        private set

    val accounts = defaultAccountRepository
        .getAllAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    val incomeCategories = defaultCategoryRepository
        .getAllIncomeCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    val expenseCategories = defaultCategoryRepository
        .getAllExpenseCategories()
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

    fun changeFromAccount(value: Account) {
        fromAccount = value
    }

    fun changeToAccount(value: Account) {
        toAccount = value
    }

    fun changeCategoryId(value: Category) {
        category = value
    }

    fun resetIds() {
        fromAccount = Account()
        toAccount = Account()
        category = Category()
    }

    fun changeTime(value: Long) {
        time = value
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

        amount = if (value.contains(regex = Regex(pattern = "^*\\.00?$")))
            String.format("%.0f", value.toDouble())
        else String.format("%.${decimalPoints}f", value.toDouble())

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
                "+" -> (a + b).toString()
                "-" -> (a - b).toString()
                "÷" -> (a / b).toString()
                "x" -> (a * b).toString()
                else -> amount
            }
        }
    }

    fun calculateAnswer() {
        changeAmount(value = operation?.invoke(answer, amount.toDouble()) ?: amount)
    }

    private fun validateInput(): Boolean {
        return (amount.toDouble() > 0.00 && fromAccount != toAccount)
    }

    private fun clear() {
        selectedIndex = 1;
        operation = null
        time = System.currentTimeMillis()
        description = ""
        resetIds()
        resetOperator()
    }

    fun save() {
        if (validateInput()) {
            viewModelScope.launch {
                withContext(context = Dispatchers.IO) {
                    defaultRepository.insert(
                        entry = Entry(
                            amount = amount.toDouble(),
                            isExpense = (selectedIndex > 1),
                            epochSeconds = time,
                            categoryId = if (selectedIndex == 2) transferCategory.value.id else category.id,
                            accountId = fromAccount.id,
                            description = description
                        )
                    )

                    if (selectedIndex > 0) {
                        defaultAccountRepository.update(
                            account = fromAccount.copy(
                                balance = fromAccount.balance - amount.toDouble()
                            )
                        )
                    } else {
                        defaultAccountRepository.update(
                            account = fromAccount.copy(
                                balance = fromAccount.balance + amount.toDouble()
                            )
                        )
                    }

                    if (toAccount != Account()) {
                        defaultAccountRepository.update(
                            account = toAccount.copy(
                                balance = toAccount.balance + amount.toDouble()
                            )
                        )
                    }
                    clear()
                }
            }
        }
    }
}