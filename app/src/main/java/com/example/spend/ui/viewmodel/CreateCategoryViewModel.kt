package com.example.spend.ui.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
    private val defaultCategoryRepository: CategoryRepository
) : ViewModel() {
    private val _category = MutableStateFlow(value = Category())
    val category = _category.asStateFlow()

    private val _selectedIndex = MutableStateFlow(value = 0)
    val selectedIndex = _selectedIndex.asStateFlow()

    private val _showSnackBar = MutableStateFlow(value = false)
    val showSnackBar = _showSnackBar.asStateFlow()

    private val _snackBarMessage = MutableStateFlow(value = "")
    val snackBarMessage = _snackBarMessage.asStateFlow()

    fun toggleShowSnackBar() {
        viewModelScope.launch {
            _showSnackBar.value = !(_showSnackBar.value)
        }
    }

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

    fun validateInput(): Boolean = _category.value.name != ""

    fun save() {
        if (validateInput()) {
            viewModelScope.launch {
                withContext(context = Dispatchers.IO) {
                    _category.value = _category.value.copy(isExpense = (_selectedIndex.value == 1))
                    try {
                        defaultCategoryRepository.insert(_category.value)
                        clear()
                        _snackBarMessage.value = "Successful Insertion"
                        _showSnackBar.value = true
                    } catch (e: SQLiteException) {
                        _snackBarMessage.value = "A category of this name or type already exists"
                        _showSnackBar.value = true
                    } catch (e: Exception) {
                        _snackBarMessage.value = "Unknown Error has occurred"
                        _showSnackBar.value = true
                    }
                }
            }
        } else {
            _snackBarMessage.value = "Error: Specify the name"
            _showSnackBar.value = true
        }
    }
}