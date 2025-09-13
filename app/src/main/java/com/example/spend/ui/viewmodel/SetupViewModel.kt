package com.example.spend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.BalanceRepository
import com.example.spend.data.datastore.config.LoginRepository
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.validateCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val defaultAccountRepository: AccountRepository,
) : ViewModel() {
    val isFirstLogin = MutableStateFlow(true)

    val initialAccount = MutableStateFlow("")

    val initialAmount = MutableStateFlow("")

    fun updateInitialAccount(accountName: String) {
        initialAccount.value = accountName
    }

    fun updateInitialAmount(amount: String) {
        initialAmount.value = amount
    }

    fun isInputCorrect() = (initialAccount.value.trim() != "" && validateCurrency(initialAmount.value))

    fun updateDetails() {
        viewModelScope.launch {
            defaultAccountRepository.insert(
                Account(
                    name = initialAccount.value,
                    balance = initialAmount.value.toDouble()
                )
            )
        }
    }
    fun getFirstLoginStatus() = loginRepository.firstLogin
    fun registerFirstLogin() {
        viewModelScope.launch {
            loginRepository.registerFirstLogin()
        }
    }

    init {
        viewModelScope.launch {
            getFirstLoginStatus()
                .collect { value ->
                    isFirstLogin.value = value
                }
        }
    }
}