package com.example.spend.ui.viewmodel.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.example.spend.data.dto.EntryCategory
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.entry.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val durationMillis = 1_000L

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val defaultAccountRepository: AccountRepository
) : ViewModel() {
    private val _selectedEntry: MutableStateFlow<EntryCategory?> = MutableStateFlow(value = null)
    val selectedEntry = _selectedEntry.asStateFlow()

    val thereAreEntries = defaultRepository.areEntriesPresent()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = false
        )

    private val _showSnackBar = MutableStateFlow(value = false)
    val showSnackBar = _showSnackBar.asStateFlow()

    private val _snackBarMessage = MutableStateFlow(value = "")
    val snackBarMessage = _snackBarMessage.asStateFlow()

    val transactions: StateFlow<List<EntryCategory>> =
        defaultRepository.getEntryIconAndColor()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
                initialValue = emptyList()
            )

    fun toggleSnackBar() {
        _showSnackBar.value = !(_showSnackBar.value)
    }

    fun selectEntry(entry: EntryCategory) {
        _selectedEntry.value = entry
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            try {
                withContext(context = Dispatchers.IO) {
                    val account = defaultAccountRepository.getAccountById(_selectedEntry.value!!.entry.accountId)
                        .firstOrNull()
                    val firstAccount = defaultAccountRepository.getFirstAccount().firstOrNull()
                    if (account != null) {
                        defaultAccountRepository.update(account.copy(balance = account.balance + (_selectedEntry.value!!.entry.amount * if (_selectedEntry.value!!.entry.isExpense) 1 else -1)))
                        if (firstAccount != null) {
                            defaultAccountRepository.update(account = firstAccount.copy(balance = firstAccount.balance + (_selectedEntry.value!!.entry.amount * if (_selectedEntry.value!!.entry.isExpense) 1 else -1)))
                        }
                        defaultRepository.delete(_selectedEntry.value!!.entry)
                    }
                }
                _snackBarMessage.value = "Successful deletion"
                _showSnackBar.value = true
            } catch (e: SQLiteException) {
                _snackBarMessage.value = "Unexpected error occurred"
                _showSnackBar.value = true
            } catch (e: Exception) {
                _snackBarMessage.value = "Unexpected error occurred"
                _showSnackBar.value = true
            }
        }
    }
}