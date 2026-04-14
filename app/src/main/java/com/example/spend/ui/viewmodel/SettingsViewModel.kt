package com.example.spend.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.domain.settings.ExportCsv
import com.example.spend.domain.settings.ResetData
import com.example.spend.ui.screen.showToast
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
    private val defaultPreferencesRepository: PreferencesRepository,
    private val resetDataUseCase: ResetData,
    private val exportCsvUseCase: ExportCsv
) : ViewModel() {
    private val notificationManagerCompat = NotificationManagerCompat.from(context)

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
        viewModelScope.launch {
            val result = exportCsvUseCase(directory = directory)
            if (result) {
                showToast(message = "Successful export")
            } else {
                showToast(message = "Failed to export files")
            }
        }
    }

    fun resetData() {
        viewModelScope.launch {
            val result = resetDataUseCase()
            if (result) {
                showToast(message = "Successful data reset")
            } else {
                showToast(message = "Failed to reset data")
            }
        }
    }
}