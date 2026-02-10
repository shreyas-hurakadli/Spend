package com.example.spend.ui.viewmodel.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.room.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

private const val DURATION_MILLIS = 1_000L

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val defaultAccountRepository: AccountRepository
): ViewModel() {
    val accounts = defaultAccountRepository.getAllAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = emptyList()
        )

    val thereAreAccounts = defaultAccountRepository.thereAreAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = false
        )
}