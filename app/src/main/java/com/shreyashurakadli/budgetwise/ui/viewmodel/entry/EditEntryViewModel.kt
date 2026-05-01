package com.shreyashurakadli.budgetwise.ui.viewmodel.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.shreyashurakadli.budgetwise.data.datastore.config.PreferencesRepository
import com.shreyashurakadli.budgetwise.data.room.account.AccountRepository
import com.shreyashurakadli.budgetwise.data.room.category.CategoryRepository
import com.shreyashurakadli.budgetwise.data.room.entry.Entry
import com.shreyashurakadli.budgetwise.data.room.entry.EntryRepository
import com.shreyashurakadli.budgetwise.domain.entry.EditTransaction
import com.shreyashurakadli.budgetwise.ui.data.MAX_ENTRY_AMOUNT
import com.shreyashurakadli.budgetwise.ui.data.MAX_ENTRY_DESCRIPTION_LENGTH
import com.shreyashurakadli.budgetwise.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val durationMillis = 1_000L

@HiltViewModel
class EditEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val entryRepository: EntryRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val preferencesRepository: PreferencesRepository,
    private val editTransactionUseCase: EditTransaction
): ViewModel() {
    val id = savedStateHandle.toRoute<Routes.EditTransactionScreen>().id

    val entry = entryRepository.getEntryCategoryById(id = id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = null
        )

    val currencySymbol = preferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = ""
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val account = entry
        .flatMapLatest { entryCategory ->
            if (entryCategory == null) {
                emptyFlow()
            } else {
                accountRepository.getAccountById(id = entryCategory.entry.accountId)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val category = entry
        .flatMapLatest { entryCategory ->
            if (entryCategory == null) {
                emptyFlow()
            } else {
                categoryRepository.getCategory(id = entryCategory.entry.categoryId)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = null
        )


    val categories = categoryRepository.getAllCategories()
        .map { list -> list.filter { it.name != "All" && it.name != "Transfer" } }
        .flowOn(context = Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = emptyList()
        )

    val accounts = accountRepository.getAllAccounts()
        .map { list -> list.filter { it.name != "All" } }
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

    fun onToastShow() {
        _showToast.value = false
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
    }

    fun validateDescription(description: String): Boolean =
        if (description.length > MAX_ENTRY_DESCRIPTION_LENGTH) {
            showToast(message = "The maximum allowed length for description is $MAX_ENTRY_DESCRIPTION_LENGTH")
            false
        } else {
            true
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

    fun editTransaction(editedEntry: Entry) {
        viewModelScope.launch {
            if (validateEditedInput(editedEntry = editedEntry)) {
                val result = editTransactionUseCase(
                    entry = entry.value?.entry ?: Entry(),
                    editedEntry = editedEntry,
                    allAccount = accountRepository.getFirstAccount().first()
                )
                if (result) {
                    showToast(message = "Successfully edited transaction")
                } else {
                    showToast(message = "Transaction editing was unsuccessful")
                }
            }
        }
    }
}