package com.example.spend.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.ui.icons
import com.example.spend.validateCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val durationMillis = 1_000L

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val defaultAccountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(Account())
    val uiState = _uiState.asStateFlow()

    private var _balance = MutableStateFlow("")
    val balance = _balance.asStateFlow()

    private val allAccount = defaultAccountRepository.getFirstAccount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(durationMillis),
            initialValue = Account()
        )

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateBalance(value: String) {
        if (validateCurrency(value)) {
            _balance.value = value
        }
    }

    fun updateColor(value: Color) {
        _uiState.value = _uiState.value.copy(color = value)
    }

    fun updateIcon(value: String) {
        _uiState.value = _uiState.value.copy(icon = value)
    }

    fun clear() {
        _uiState.value = Account()
        _balance.value = ""
    }

    fun save(balance: String) {
        if (validateCurrency(balance)) {
            _uiState.value = _uiState.value.copy(balance = balance.toDouble())
        }
        viewModelScope.launch {
            if (allAccount.value != Account()) {
                defaultAccountRepository.insert(_uiState.value)
                defaultAccountRepository.update(
                    account = allAccount.value.copy(
                        balance = balance.toDouble() + allAccount.value.balance
                    )
                )
            }
            clear()
        }
    }
}