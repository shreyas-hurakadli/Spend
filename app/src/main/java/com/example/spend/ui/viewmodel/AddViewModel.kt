package com.example.spend.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.account.DefaultAccountRepository
import com.example.spend.data.room.category.DefaultCategoryRepository
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.ZoneId
import javax.inject.Inject

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
}