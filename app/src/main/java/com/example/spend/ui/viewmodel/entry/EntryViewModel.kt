package com.example.spend.ui.viewmodel.entry

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.dto.EntryCategory
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.longToDate
import com.example.spend.ui.MAX_ENTRY_AMOUNT
import com.example.spend.ui.MAX_ENTRY_DESCRIPTION_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val durationMillis = 1_000L

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val defaultAccountRepository: AccountRepository,
    private val defaultCategoryRepository: CategoryRepository,
    private val defaultPreferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _selectedEntry: MutableStateFlow<EntryCategory?> = MutableStateFlow(value = null)
    val selectedEntry = _selectedEntry.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedEntryAccount = selectedEntry
        .flatMapLatest {
            defaultAccountRepository.getAccountById(it?.entry?.accountId ?: 0L)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
                    initialValue = null
                )
        }

    val currencySymbol = defaultPreferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = ""
        )

    val thereAreEntries = defaultRepository.areEntriesPresent()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = false
        )

    val accounts = defaultAccountRepository.getAllAccounts()
        .map { list -> list.filter { it.name != "All" } }
        .flowOn(context = Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = emptyList()
        )

    val categories = if (_selectedEntry.value?.entry?.isExpense
            ?: false
    ) defaultCategoryRepository.getAllExpenseCategories()
    else defaultCategoryRepository.getAllIncomeCategories()
        .map { list -> list.filter { it.name != "All" && it.name != "Transfer" } }
        .flowOn(context = Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = emptyList()
        )

    private val _showSnackBar = MutableStateFlow(value = false)
    val showSnackBar = _showSnackBar.asStateFlow()

    private val _snackBarMessage = MutableStateFlow(value = "")
    val snackBarMessage = _snackBarMessage.asStateFlow()

    val transactions =
        defaultRepository.getEntryIconAndColor()
            .map {
                it.groupBy { entryCategory -> longToDate(longDate = entryCategory.entry.epochSeconds) }
            }
            .flowOn(context = Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
                initialValue = emptyMap()
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
                val account =
                    defaultAccountRepository.getAccountById(_selectedEntry.value!!.entry.accountId)
                        .firstOrNull()
                val firstAccount = defaultAccountRepository.getFirstAccount().firstOrNull()
                if (account != null) {
                    defaultAccountRepository.update(account.copy(balance = account.balance + (_selectedEntry.value!!.entry.amount * if (_selectedEntry.value!!.entry.isExpense) 1 else -1)))
                    if (firstAccount != null) {
                        defaultAccountRepository.update(account = firstAccount.copy(balance = firstAccount.balance + (_selectedEntry.value!!.entry.amount * if (_selectedEntry.value!!.entry.isExpense) 1 else -1)))
                    }
                    defaultRepository.delete(_selectedEntry.value!!.entry)
                }
                showSnackBarMessage(message = "Successful deletion")
            } catch (e: SQLiteException) {
                showSnackBarMessage(message = "Unexpected error occurred")
            } catch (e: Exception) {
                showSnackBarMessage(message = "Unexpected error occurred")
            }
        }
    }

    fun showSnackBarMessage(message: String) {
        _snackBarMessage.value = message
        _showSnackBar.value = true
    }

    private fun validateEditedInput(editedEntry: Entry): Boolean =
        with(receiver = editedEntry) {
            if (amount > 0.00 && amount <= MAX_ENTRY_AMOUNT) {
                true
            } else if (description.length <= MAX_ENTRY_DESCRIPTION_LENGTH) {
                true
            } else {
                false
            }
        }

    fun validateDescription(description: String): Boolean =
        if (description.length > MAX_ENTRY_DESCRIPTION_LENGTH) {
            showSnackBarMessage(message = "The maximum allowed length for description is $MAX_ENTRY_DESCRIPTION_LENGTH")
            false
        } else {
            true
        }


    fun editTransaction(editedEntry: Entry) {
        viewModelScope.launch {
            try {
                if (validateEditedInput(editedEntry = editedEntry)) {
                    val change = editedEntry.amount - (_selectedEntry.value?.entry?.amount ?: 0.00)
                    val firstAccount = defaultAccountRepository.getFirstAccount().firstOrNull()
                    firstAccount?.let {
                        defaultRepository.update(entry = editedEntry)
                        defaultAccountRepository.update(
                            account = it.copy(
                                balance = it.balance + change
                            )
                        )
                        val editedAccount = defaultAccountRepository.getAccountById(id = editedEntry.accountId).firstOrNull()
                        editedAccount?.let { acct ->
                            defaultAccountRepository.update(
                                account = acct.copy(balance = acct.balance + change)
                            )
                        }
                        _selectedEntry.value = _selectedEntry.value?.copy(entry = editedEntry)
                        showSnackBarMessage(message = "Successful transaction editing")
                    }
                } else {
                    showSnackBarMessage(message = "Specify the fields correctly")
                }
            } catch (e: SQLiteException) {
                showSnackBarMessage(message = "Failed to edit transaction")
            } catch (e: Exception) {
                showSnackBarMessage(message = "Failed to edit transaction")
            }
        }
    }
}