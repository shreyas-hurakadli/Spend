package com.example.spend.ui.viewmodel.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.dto.EntryCategory
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.ui.data.MAX_CATEGORY_NAME_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DURATION_MILLIS = 1_000L

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val defaultEntryRepository: EntryRepository,
    private val defaultCategoryRepository: CategoryRepository,
    private val defaultPreferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _selectedCategory = MutableStateFlow(value = Category())
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _showToast = MutableStateFlow(value = false)
    val showToast = _showToast.asStateFlow()

    private val _toastMessage = MutableStateFlow(value = "")
    val toastMessage = _toastMessage.asStateFlow()

    val categories = defaultCategoryRepository.getAllCategories()
        .map { list -> list.filter { it.name != "All" && it.name != "Transfer" } }
        .flowOn(context = Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryTransactions = _selectedCategory
        .flatMapLatest { category ->
            when (category) {
                Category() -> emptyFlow()
                else -> defaultEntryRepository.getEntriesByCategoryId(id = category.id)
            }
        }
        .map { list ->
            list.map { entry ->
                EntryCategory(
                    entry = entry,
                    name = _selectedCategory.value.name,
                    icon = _selectedCategory.value.icon,
                    color = _selectedCategory.value.color
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = emptyList()
        )

    val totalCategoryAmount = categoryTransactions
        .map { list ->
            list.fold(initial = 0.00) { prev, entryCategory ->
                prev + entryCategory.entry.amount
            }
        }
        .flowOn(context = Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = 0.00
        )

    val currencySymbol = defaultPreferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = ""
        )

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
    }

    fun onToastShown() {
        _showToast.value = false
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                defaultCategoryRepository.delete(category = category)
            } catch (e: Exception) {
                showToast(message = "Failed to delete category")
            }
        }
    }

    private fun validateEditedCategory(editedCategory: Category): Boolean =
        if (editedCategory.name.isBlank()) {
            showToast(message = "Category name should not be blank")
            false
        } else if (editedCategory.name.length > MAX_CATEGORY_NAME_LENGTH) {
            showToast(message = "Category name length should not be more than $MAX_CATEGORY_NAME_LENGTH")
            false
        } else {
            true
        }

    fun editCategory(editedCategory: Category) {
        viewModelScope.launch {
            try {
                if (validateEditedCategory(editedCategory = editedCategory)) {
                    defaultCategoryRepository.update(category = editedCategory.copy(name = editedCategory.name.trim()))
                    _selectedCategory.value = editedCategory
                    showToast(message = "Category editing successful")
                }
            } catch (e: Exception) {
                showToast(message = "Failed to edit category")
            }
        }
    }
}