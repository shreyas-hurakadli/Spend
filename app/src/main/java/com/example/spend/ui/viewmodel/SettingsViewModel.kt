package com.example.spend.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.local.file.CsvExportableRepository
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.budget.Budget
import com.example.spend.data.room.budget.BudgetRepository
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.currency.CurrencyRepository
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DURATION_MILLIS = 1_000L

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val defaultEntryRepository: EntryRepository,
    private val defaultAccountRepository: AccountRepository,
    private val defaultCategoryRepository: CategoryRepository,
    private val defaultBudgetRepository: BudgetRepository,
    private val defaultCurrencyRepository: CurrencyRepository,
    private val defaultCsvExportableRepository: CsvExportableRepository,
    private val defaultPreferencesRepository: PreferencesRepository
) : ViewModel() {
    private val notificationManagerCompat = NotificationManagerCompat.from(context)

    private val selectedDirectory = MutableStateFlow(value = Uri.EMPTY)
    private val _notificationPermissionTurnedOn =
        MutableStateFlow(value = areNotificationsEnabled())
    val notificationPermissionTurnedOn = _notificationPermissionTurnedOn.asStateFlow()

    private val _showNotificationRequestPermissionDialog = MutableStateFlow(value = false)
    val showNotificationRequestPermissionDialog =
        _showNotificationRequestPermissionDialog.asStateFlow()

    private val _permissionPermanentlyDismissed = MutableStateFlow(value = false)

    private val _showDeleteDialogBox = MutableStateFlow(value = false)
    val showDeleteDialogBox = _showDeleteDialogBox.asStateFlow()

    private val _openSettingsEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val openSettingsEvent = _openSettingsEvent.asSharedFlow()

    private val _showToast = MutableStateFlow(value = false)
    val showToast = _showToast.asStateFlow()

    private val _toastMessage = MutableStateFlow(value = "")
    val toastMessage = _toastMessage.asStateFlow()

    private val _selectedTimeFormat = MutableStateFlow(value = "12h")
    val selectedTimeFormat = _selectedTimeFormat.asStateFlow()

    private val _showCurrencySheet = MutableStateFlow(value = false)
    val showCurrencySheet = _showCurrencySheet.asStateFlow()

    val currencySymbol = defaultPreferencesRepository
        .baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = "$"
        )

    val currency = defaultPreferencesRepository
        .baseCurrency
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = "$"
        )

    init {
        viewModelScope.launch {
            _selectedTimeFormat.value = defaultPreferencesRepository.timeFormat.first()
        }
    }

    fun changeTimeFormat(format: String) {
        _selectedTimeFormat.value = format
        viewModelScope.launch {
            defaultPreferencesRepository.changeTimeFormat(format = format)
        }
    }

    private fun areNotificationsEnabled(): Boolean =
        notificationManagerCompat.areNotificationsEnabled()

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
    }

    fun toggleShowCurrencySheet() {
        _showCurrencySheet.value = !_showCurrencySheet.value
    }

    fun toggleShowNotificationRequestPermissionDialog(turnOn: Boolean) {
        if (turnOn && !_permissionPermanentlyDismissed.value) {
            _showNotificationRequestPermissionDialog.value = true
        } else {
            _openSettingsEvent.tryEmit(value = Unit)
        }
        _notificationPermissionTurnedOn.value = areNotificationsEnabled()
    }

    fun toggleShowDeleteDialogBox() {
        _showDeleteDialogBox.value = !_showDeleteDialogBox.value
    }

    fun onPermissionRequestDismissed(permissionPermanentlyDenied: Boolean) {
        _permissionPermanentlyDismissed.value = permissionPermanentlyDenied
        _showNotificationRequestPermissionDialog.value = false
        _notificationPermissionTurnedOn.value = areNotificationsEnabled()
    }

    private fun registerDirectory(directory: Uri) {
        selectedDirectory.value = directory
    }

    fun onCurrencySelect(currency: String) {
        viewModelScope.launch {
            try {
                defaultPreferencesRepository.registerBaseCurrency(baseCurrency = currency)
            } catch (e: Exception) {
                showToast(message = "Failed to change currency")
            }
        }
    }

    fun onToastShown() {
        _showToast.value = false
    }

    fun exportCsvFiles(directory: Uri) {
        try {
            registerDirectory(directory = directory)
            viewModelScope.launch {
                val entries = defaultEntryRepository.getAllEntries().first()
                defaultCsvExportableRepository.writeFile(
                    parentDirectory = selectedDirectory.value,
                    fileName = "entries.csv",
                    header = Entry.HEADER,
                    data = entries
                )
                val categories = defaultCategoryRepository.getAllCategories().first()
                defaultCsvExportableRepository.writeFile(
                    parentDirectory = selectedDirectory.value,
                    fileName = "categories.csv",
                    header = Category.HEADER,
                    data = categories
                )
                val accounts = defaultAccountRepository.getAllAccounts().first()
                defaultCsvExportableRepository.writeFile(
                    parentDirectory = selectedDirectory.value,
                    fileName = "accounts.csv",
                    header = Account.HEADER,
                    data = accounts
                )
                val budgets = defaultBudgetRepository.getAllBudgets().first()
                defaultCsvExportableRepository.writeFile(
                    parentDirectory = selectedDirectory.value,
                    fileName = "budgets.csv",
                    header = Budget.HEADER,
                    data = budgets
                )
                showToast(message = "Successful export")
            }
        } catch (e: Exception) {
            showToast(message = "Failed to export export")
        }
    }

    fun resetData() {
        viewModelScope.launch {
            try {
                defaultEntryRepository.deleteAll()
                defaultAccountRepository.resetData()
                defaultCategoryRepository.resetData()
                defaultBudgetRepository.deleteAll()
                defaultCurrencyRepository.deleteAll()
                showToast(message = "Successful data reset")
            } catch (e: SQLiteException) {
                showToast(message = "Failed to reset data")
            } catch (e: Exception) {
                showToast(message = "An unknown error has occurred")
            }
        }
    }
}