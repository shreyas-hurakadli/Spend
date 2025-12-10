package com.example.spend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.dto.EntryCategory
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.entry.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val durationMillis = 1_000L

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val defaultAccountRepository: AccountRepository,
) : ViewModel() {
    val transactions: StateFlow<List<EntryCategory>> =
        defaultRepository.getEntryIconAndColor(limit = 4)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(durationMillis),
                initialValue = emptyList()
            )
    val accountList: StateFlow<List<Account>> =
        defaultAccountRepository.getAllAccounts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(durationMillis),
                initialValue = emptyList()
            )

    val currentAccount =
        defaultAccountRepository.getFirstAccount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = Account()
            )
}