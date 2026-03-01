package com.example.spend.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.budget.BudgetRepository
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.currency.CurrencyRepository
import com.example.spend.data.room.entry.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val defaultEntryRepository: EntryRepository,
    private val defaultAccountRepository: AccountRepository,
    private val defaultCategoryRepository: CategoryRepository,
    private val defaultBudgetRepository: BudgetRepository,
    private val defaultCurrencyRepository: CurrencyRepository
) : ViewModel() {
    private val notificationManagerCompat = NotificationManagerCompat.from(context)
    private val _notificationPermissionTurnedOn = MutableStateFlow(value = areNotificationsEnabled())
    val notificationPermissionTurnedOn = _notificationPermissionTurnedOn.asStateFlow()

    private val _showNotificationRequestPermissionDialog = MutableStateFlow(value = false)
    val showNotificationRequestPermissionDialog = _showNotificationRequestPermissionDialog.asStateFlow()

    private val _permissionPermanentlyDismissed = MutableStateFlow(value = false)

    private val _showDeleteDialogBox = MutableStateFlow(value = false)
    val showDeleteDialogBox = _showDeleteDialogBox.asStateFlow()

    private val _openSettingsEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val openSettingsEvent = _openSettingsEvent.asSharedFlow()

    private val _showSnackBar = MutableStateFlow(value = false)
    val showSnackBar = _showSnackBar.asStateFlow()

    private val _snackBarMessage = MutableStateFlow(value = "")
    val snackBarMessage = _snackBarMessage.asStateFlow()

    private fun areNotificationsEnabled(): Boolean = notificationManagerCompat.areNotificationsEnabled()

    fun toggleShowSnackBar() {
        _showSnackBar.value = !_showSnackBar.value
    }

    fun toggleShowNotificationRequestPermissionDialog(turnOn: Boolean) {
        Log.d(
            "SettingsScreen",
            "Inside viewModel input = $turnOn"
        )
        if (turnOn && !_permissionPermanentlyDismissed.value) {
            _showNotificationRequestPermissionDialog.value = true
            Log.d(
                "SettingsScreen",
                "Inside true block = ${showNotificationRequestPermissionDialog.value}"
            )
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

    fun resetData() {
        viewModelScope.launch {
            try {
                defaultEntryRepository.deleteAll()
                defaultAccountRepository.resetData()
                defaultCategoryRepository.resetData()
                defaultBudgetRepository.deleteAll()
                defaultCurrencyRepository.deleteAll()
                _snackBarMessage.value = "Successful data reset"
                toggleShowSnackBar()
            } catch (e: SQLiteException) {
                _snackBarMessage.value = "Failed to reset data"
                toggleShowSnackBar()
            } catch (e: Exception) {
                _snackBarMessage.value = "An unknown error has occurred"
                toggleShowSnackBar()
            }
        }
    }
}