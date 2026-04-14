package com.example.spend.ui.viewmodel.category

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.domain.category.AddCategory
import com.example.spend.ui.data.MAX_CATEGORY_NAME_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
    private val defaultCategoryRepository: CategoryRepository,
    private val addCategoryUseCase: AddCategory
) : ViewModel() {
    private val _category = MutableStateFlow(value = Category())
    val category = _category.asStateFlow()

    private val _selectedIndex = MutableStateFlow(value = 0)
    val selectedIndex = _selectedIndex.asStateFlow()

    private val _showToast = MutableStateFlow(value = false)
    val showToast = _showToast.asStateFlow()

    private val _toastMessage = MutableStateFlow(value = "")
    val toastMessage = _toastMessage.asStateFlow()

    fun changeSelectedIndex() {
        _selectedIndex.value = if (_selectedIndex.value == 1) 0 else 1
    }

    fun changeName(name: String) {
        _category.value = _category.value.copy(name = name)
    }

    fun changeColor(color: Color) {
        _category.value = _category.value.copy(color = color)
    }

    fun changeLogo(logo: String) {
        _category.value = _category.value.copy(icon = logo)
    }

    fun clear() {
        _category.value = Category()
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        _showToast.value = true
    }

    fun onToastShown() {
        _showToast.value = false
    }

    fun validateInput(): Boolean =
        if (_category.value.name.isBlank()) {
            showToast(message = "Name cannot be blank")
            false
        } else if (_category.value.name.length > MAX_CATEGORY_NAME_LENGTH) {
            showToast(message = "Name length cannot be more than $MAX_CATEGORY_NAME_LENGTH")
            false
        } else {
            true
        }

    fun save() {
        if (validateInput()) {
            _category.value = _category.value.copy(isExpense = (_selectedIndex.value == 1))
            viewModelScope.launch {
                val result = addCategoryUseCase(
                    category = _category.value
                )
                if (result) {
                    clear()
                    showToast(message = "Successfully created the category")
                } else {
                    showToast(message = "Failed to create category")
                }
            }
        }
    }
}