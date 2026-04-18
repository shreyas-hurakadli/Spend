package com.example.spend.ui.viewmodel.budget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.budget.Budget
import com.example.spend.data.room.budget.BudgetRepository
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.domain.budget.EditBudget
import com.example.spend.ui.data.MAX_BUDGET_NAME_LENGTH
import com.example.spend.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TIMEOUT_MILLIS = 1_000L

@HiltViewModel
class EditBudgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val budgetRepository: BudgetRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val preferencesRepository: PreferencesRepository,
    private val editBudgetUseCase: EditBudget
): ViewModel() {
    private val id = savedStateHandle.toRoute<Routes.EditBudgetScreen>().id

    val budget = budgetRepository.getBudgetById(id = id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = null
        )
    private val _showToast = MutableStateFlow(value = false)
    val showToast = _showToast.asStateFlow()

    private val _toastMessage = MutableStateFlow(value = "")
    val toastMessage = _toastMessage.asStateFlow()

    val currencySymbol = preferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = ""
        )

    val currencyCode = preferencesRepository.baseCurrency
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = ""
        )

    val accounts = accountRepository.getAllAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    val categories = categoryRepository.getAllExpenseCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
    }

    fun onToastShow() {
        _showToast.value = false
    }

    private fun validateEditedBudget(editedBudget: Budget): Boolean =
        if (editedBudget.name.length > MAX_BUDGET_NAME_LENGTH) {
            showToast(message = "Budget name length should be less than ${MAX_BUDGET_NAME_LENGTH + 1}")
            false
        } else if (editedBudget.period <= 0) {
            showToast(message = "Invalid period specification")
            false
        } else {
            true
        }

    fun editBudget(editedBudget: Budget) {
        if (validateEditedBudget(editedBudget)) {
            viewModelScope.launch {
                val result = editBudgetUseCase(editedBudget = editedBudget)
                if (result) {
                    showToast(message = "Successfully edited budget")
                } else {
                    showToast(message = "Failed to edit budget")
                }
            }
        } else {
            showToast(message = "Specify the fields correctly")
        }
    }
}