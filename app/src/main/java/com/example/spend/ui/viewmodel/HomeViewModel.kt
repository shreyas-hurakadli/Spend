package com.example.spend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.BalanceRepository
import com.example.spend.data.room.Entry
import com.example.spend.data.room.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val dataStoreRepository: BalanceRepository
) : ViewModel() {
    val balance: StateFlow<Double> =
        dataStoreRepository.balance
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = 0.00
            )

    val transactions: StateFlow<List<Entry>> =
        defaultRepository.getRecentEntries()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = emptyList()
            )

    fun updateBalance(balance: String) {
        if (balance != "") {
            viewModelScope.launch {
                dataStoreRepository.saveBalance(balance.toDouble())
            }
        }
    }

    fun truncateTable() {
        viewModelScope.launch {
            defaultRepository.deleteAll()
            defaultRepository.resetAutoincrement()
            dataStoreRepository.saveBalance(0.00)
        }
    }
}