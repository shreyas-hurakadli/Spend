package com.example.spend.ui.viewmodel.category

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.dto.EntryCategory
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.domain.category.DeleteCategory
import com.example.spend.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val durationMillis = 1_000L

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val entryRepository: EntryRepository,
    private val categoryRepository: CategoryRepository,
    private val preferencesRepository: PreferencesRepository,
    private val deleteCategoryUseCase: DeleteCategory
) : ViewModel() {
    private val id = savedStateHandle.toRoute<Routes.CategoryDetailScreen>().id

    val category = categoryRepository.getCategory(id = id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = category
        .flatMapLatest { category ->
            if (category == null) {
                emptyFlow()
            } else {
                entryRepository.getEntriesByCategoryId(id = category.id)
            }
        }
        .map { list ->
            list.map { entry ->
                EntryCategory(
                    entry = entry,
                    name = category.value?.name ?: "",
                    icon = category.value?.icon ?: "",
                    color = category.value?.color ?: Color.White
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = emptyList()
        )

    val amount = categoryRepository.getExpenseByCategoryId(id = category.value?.id ?: -1L)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = 0.00
        )

    val currencySymbol = preferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = ""
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

    fun deleteCategory() {
        Log.d("DeleteCategory", "DeleteCategory method")
        viewModelScope.launch {
            val result = deleteCategoryUseCase(category = category.value ?: Category())
            if (!result) {
                showToast(message = "Failed to delete category")
            }
        }
    }
}