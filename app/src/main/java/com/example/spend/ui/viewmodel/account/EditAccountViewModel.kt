package com.example.spend.ui.viewmodel.account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.domain.account.EditAccount
import com.example.spend.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val durationMillis = 1_000L

@HiltViewModel
class EditAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val accountRepository: AccountRepository,
    private val editAccountUseCase: EditAccount
) : ViewModel() {
    val id = savedStateHandle.toRoute<Routes.EditAccountScreen>().id

    val account = accountRepository.getAccountById(id = id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = null
        )

    private val _showToast = MutableStateFlow(value = false)
    val showToast = _showToast.asStateFlow()

    private val _toastMessage = MutableStateFlow(value = "")
    val toastMessage = _toastMessage.asStateFlow()

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
    }

    fun onToastShown() {
        _showToast.value = false
    }

    fun editAccount(editedAccount: Account) {
        account.value?.let { account ->
            viewModelScope.launch {
                val result = editAccountUseCase(
                    account = account,
                    editedAccount = editedAccount,
                )
                if (result) {
                    showToast(message = "Account is successfully edited")
                } else {
                    showToast(message = "Account could not be edited")
                }
            }
        }
    }
}