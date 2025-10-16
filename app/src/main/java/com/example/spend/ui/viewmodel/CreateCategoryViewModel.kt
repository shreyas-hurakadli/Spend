package com.example.spend.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
    private val defaultCategoryRepository: CategoryRepository
) : ViewModel() {
    var category = MutableStateFlow(value = Category())
        private set

    var selectedIndex = MutableStateFlow(value = 0)
        private set

    fun changeSelectedIndex() {
        selectedIndex.value = if (selectedIndex.value == 1) 0 else 1
        category.value = category.value.copy(isExpense = selectedIndex.value == 1)
    }

    fun changeName(name: String) {
        category.value = category.value.copy(name = name)
    }

    fun changeColor(color: Color) {
        category.value = category.value.copy(color = color)
    }

    fun changeLogo(logo: String) {
        category.value = category.value.copy(icon = logo)
    }

    fun clear() {
        category.value = Category()
    }

    fun save() {
        viewModelScope.launch {
            withContext(context = Dispatchers.IO) {
                defaultCategoryRepository.insert(category.value)
                clear()
            }
        }
    }
}