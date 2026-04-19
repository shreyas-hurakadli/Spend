package com.example.spend.ui.viewmodel.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.domain.category.EditCategory
import com.example.spend.ui.data.MAX_CATEGORY_NAME_LENGTH
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
class EditCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val editCategoryUseCase: EditCategory
) : ViewModel() {
    private val id = savedStateHandle.toRoute<Routes.EditCategoryScreen>().id

    val category = categoryRepository.getCategory(id = id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = null
        )

    private val _showToast = MutableStateFlow(value = false)
    val showToast = _showToast.asStateFlow()

    private val _toastMessage = MutableStateFlow(value = "")
    val toastMessage = _toastMessage.asStateFlow()

    fun onToastShown() {
        _showToast.value = false
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
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
        if (validateEditedCategory(editedCategory = editedCategory)) {
            viewModelScope.launch {
                val result = editCategoryUseCase(editedCategory = editedCategory)
                if (result) {
                    showToast(message = "Category editing successful")
                } else {
                    showToast(message = "Failed to edit category")
                }
            }
        }
    }
}