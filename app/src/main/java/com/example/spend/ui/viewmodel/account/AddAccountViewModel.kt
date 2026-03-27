package com.example.spend.ui.viewmodel.account

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.ui.MAX_ACCOUNT_NAME_LENGTH
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

private const val DURATION_MILLIS = 1_000L

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val defaultAccountRepository: AccountRepository,
    private val defaultPreferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(Account())
    val uiState = _uiState.asStateFlow()

    private var _balance = MutableStateFlow("")
    val balance = _balance.asStateFlow()

    private val _showToast = MutableStateFlow(value = false)
    val showToast = _showToast.asStateFlow()

    private val _toastMessage = MutableStateFlow(value = "")
    val toastMessage = _toastMessage.asStateFlow()

    val currencySymbol = defaultPreferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = ""
        )

    val currencyCode = defaultPreferencesRepository.baseCurrency
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = ""
        )

    init {
        viewModelScope.launch {
            withContext(context = Dispatchers.IO) {
                defaultAccountRepository.getFirstAccount()
                    .collect {
                        allAccount.value = it
                    }
            }
        }
    }

    private val allAccount = MutableStateFlow(value = Account())

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateBalance(value: String) {
        if (validateCurrency(value)) {
            _balance.value = value
        } else if (value.isEmpty()) {
            _balance.value = ""
        }
    }

    fun updateColor(value: Color) {
        _uiState.value = _uiState.value.copy(color = value)
    }

    fun updateIcon(value: String) {
        _uiState.value = _uiState.value.copy(icon = value)
    }

    fun onToastShow() {
        _showToast.value = false
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
    }

    fun clear() {
        _uiState.value = Account()
        _balance.value = ""
    }

    fun checkBalance(balance: String): Boolean {
        val number = if (balance.isNotEmpty()) balance.toDouble() else 0.00
        return number > MAX_ENTRY_AMOUNT
    }

    private fun validateInput(balance: String): Boolean =
        if (_uiState.value.name.length > MAX_ACCOUNT_NAME_LENGTH) {
            showToast(message = "Name length should not exceed $MAX_ACCOUNT_NAME_LENGTH")
            false
        } else if (balance.isBlank()) {
            showToast(message = "Amount should not be blank")
            false
        } else if (balance.toDouble() > MAX_ENTRY_AMOUNT) {
            showToast(message = "Amount should not exceed $MAX_ENTRY_AMOUNT")
            false
        } else if (!validateCurrency(input = balance)) {
            showToast(message = "Amount input is invalid")
            false
        } else if (_uiState.value.name.isBlank()) {
            showToast(message = "Name should not be blank")
            false
        } else {
            true
        }

    fun save(balance: String) {
        if (validateInput(balance)) {
            _uiState.value = _uiState.value.copy(balance = balance.toDouble())
            viewModelScope.launch {
                if (allAccount.value != Account()) {
                    try {
                        defaultAccountRepository.insert(account = _uiState.value)
                        defaultAccountRepository.update(
                            account = allAccount.value.copy(
                                balance = balance.toDouble() + allAccount.value.balance
                            )
                        )
                        clear()
                        showToast(message = "Account is created successfully")
                    } catch (e: SQLiteException) {
                        showToast(message = "Account with the same name exists")
                    } catch (e: Exception) {
                        showToast(message = "An unknown error has occurred")
                    }
                } else {
                    showToast(message = "An unknown error has occurred")
                }
            }
        }
    }
}