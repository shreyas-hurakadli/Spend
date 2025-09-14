package com.example.spend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val defaultAccountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(Account())
    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateBalance(balance: Double) {
        _uiState.value = _uiState.value.copy(balance = balance)
    }

    private fun clear() {
        _uiState.value = _uiState.value.copy(name = "")
    }

    fun insertData() {
        viewModelScope.launch {
            defaultAccountRepository.insert(_uiState.value)
            clear()
        }
    }
}