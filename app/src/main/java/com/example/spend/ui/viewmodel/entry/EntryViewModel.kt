package com.example.spend.ui.viewmodel.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.dto.EntryCategory
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.domain.DeleteTransaction
import com.example.spend.longToDate
import com.example.spend.ui.data.MAX_ENTRY_AMOUNT
import com.example.spend.ui.data.MAX_ENTRY_DESCRIPTION_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val defaultPreferencesRepository: PreferencesRepository,
    private val deleteTransactionUseCase: DeleteTransaction
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

    private val _showToast = MutableStateFlow(value = false)
    val showToast = _showToast.asStateFlow()

    private val _toastMessage = MutableStateFlow(value = "")
    val toastMessage = _toastMessage.asStateFlow()

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

    fun onToastShow() {
        _showToast.value = false
    }

    fun selectEntry(entry: EntryCategory) {
        _selectedEntry.value = entry
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            val result = deleteTransactionUseCase(
                entry = _selectedEntry.value?.entry ?: Entry(),
                accountId = _selectedEntry.value?.entry?.accountId ?: -1L
            )
            if (result) {
                showToast(message = "Entry successfully deleted")
            } else {
                showToast(message = "Unexpected error occurred")
            }
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
    }

    private fun validateEditedInput(editedEntry: Entry): Boolean =
        with(receiver = editedEntry) {
            if (amount > 0.00 && amount <= MAX_ENTRY_AMOUNT) {
                true
            } else if (description.length <= MAX_ENTRY_DESCRIPTION_LENGTH) {
                true
            } else {
                showToast(message = "Specify the fields correctly")
                false
            }
        }

    fun validateDescription(description: String): Boolean =
        if (description.length > MAX_ENTRY_DESCRIPTION_LENGTH) {
            showToast(message = "The maximum allowed length for description is $MAX_ENTRY_DESCRIPTION_LENGTH")
            false
        } else {
            true
        }


    fun editTransaction(editedEntry: Entry) {
        viewModelScope.launch {
            try {
                if (validateEditedInput(editedEntry = editedEntry)) {
                    val change = editedEntry.amount - (_selectedEntry.value?.entry?.amount ?: 0.00)
                    val allAccount = defaultAccountRepository.getFirstAccount().first()

                    if (_selectedEntry.value?.entry?.accountId != editedEntry.accountId) {
                        val editedAccount =
                            defaultAccountRepository.getAccountById(id = editedEntry.accountId)
                                .first()
                        val prevAccount =
                            defaultAccountRepository.getAccountById(
                                id = _selectedEntry.value?.entry?.accountId ?: 0
                            )
                                .first()

                        if (change != 0.00) {
                            editedAccount?.let {
                                defaultAccountRepository.update(
                                    account = it.copy(balance = it.balance + editedEntry.amount)
                                )
                            }
                        } else {
                            editedAccount?.let {
                                defaultAccountRepository.update(
                                    account = it.copy(
                                        balance = it.balance + (_selectedEntry.value?.entry?.amount
                                            ?: 0.00)
                                    )
                                )
                            }
                        }

                        prevAccount?.let {
                            defaultAccountRepository.update(
                                account = it.copy(
                                    balance = it.balance - (_selectedEntry.value?.entry?.amount
                                        ?: 0.00)
                                )
                            )
                        }
                    } else {
                        if (change != 0.00) {
                            val curAccount =
                                defaultAccountRepository.getAccountById(id = editedEntry.accountId)
                                    .first()
                            curAccount?.let {
                                defaultAccountRepository.update(
                                    account = it.copy(balance = it.balance + change)
                                )
                            }
                        }
                    }

                    if (change != 0.00) {
                        defaultAccountRepository.update(
                            account = allAccount.copy(balance = allAccount.balance + change)
                        )
                    }

                    defaultRepository.update(entry = editedEntry)
                    _selectedEntry.value = _selectedEntry.value?.copy(entry = editedEntry)
                    showToast(message = "Transaction editing successful")
                }
            } catch (e: SQLiteException) {
                showToast(message = "Failed to edit transaction")
            } catch (e: Exception) {
                showToast(message = "Failed to edit transaction")
            }
        }
    }
}