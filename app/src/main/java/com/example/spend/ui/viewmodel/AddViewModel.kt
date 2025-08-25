package com.example.spend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.BalanceRepository
import com.example.spend.data.room.Entry
import com.example.spend.data.room.EntryRepository
import com.example.spend.getTodayStart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val dataStoreRepository: BalanceRepository
) : ViewModel() {
    var uiState = MutableStateFlow(Entry())
        private set

    var showSnackBar = MutableStateFlow(false)
        private set

    var amount = MutableStateFlow("")
        private set


    fun updateIsExpense(input: Boolean) {
        uiState.value = uiState.value.copy(isExpense = input)
    }

    fun updateAmount(amount: String) {
        this.amount.value = amount
    }

    fun updateSnackBarStatus() {
        showSnackBar.value = !showSnackBar.value
    }

    fun updateTag(tag: String) {
        uiState.value = uiState.value.copy(category = tag)
    }

    fun updateDescription(description: String) {
        uiState.value = uiState.value.copy(description = description)
    }

    fun updateBill(amount: Double) {
        uiState.value = uiState.value.copy(amount = amount)
    }

    fun updateDate() {
        uiState.value = uiState.value.copy(epochSeconds = getTodayStart())
    }

    private fun clear() {
        uiState.value = Entry()
    }

    fun insertData() {
        viewModelScope.launch {
            defaultRepository.insert(uiState.value)
            dataStoreRepository.saveBalance(dataStoreRepository.balance.first() - if (uiState.value.isExpense) uiState.value.amount else -uiState.value.amount)
            clear()
            updateSnackBarStatus()
        }
    }
}