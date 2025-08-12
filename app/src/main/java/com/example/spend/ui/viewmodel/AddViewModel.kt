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
): ViewModel() {
    var uiState = MutableStateFlow(Entry())
        private set

    var showSnackBar = MutableStateFlow(false)
        private set

    fun updateSnackBarStatus() {
        showSnackBar.value = !showSnackBar.value
    }

    fun updateTag(tag: String) {
        uiState.value = uiState.value.copy(tag = tag)
    }

    fun updateDescription(description: String) {
        uiState.value = uiState.value.copy(description = description)
    }

    fun updateBill(bill: Int) {
        uiState.value = uiState.value.copy(bill = bill)
    }

    fun updateDate() {
        uiState.value = uiState.value.copy(date = getTodayStart())
    }

    fun inputIsValid(): Boolean {
        return (uiState.value.bill != 0)
    }

    private fun clear() {
        uiState.value = Entry()
    }

    fun insertData() {
        viewModelScope.launch {
            defaultRepository.insert(uiState.value)
            dataStoreRepository.saveBalance(dataStoreRepository.balance.first() - uiState.value.bill)
            clear()
            updateSnackBarStatus()
        }
    }
}