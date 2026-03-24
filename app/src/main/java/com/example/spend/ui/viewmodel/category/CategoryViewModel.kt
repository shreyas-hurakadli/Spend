package com.example.spend.ui.viewmodel.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.entry.EntryRepository
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
import javax.inject.Inject

private const val DURATION_MILLIS = 1_000L

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val defaultEntryRepository: EntryRepository,
    private val defaultCategoryViewModel: CategoryRepository
) : ViewModel() {
    private val _selectedCategory = MutableStateFlow(value = Category())
    val selectedCategory = _selectedCategory.asStateFlow()

    val categories = defaultCategoryViewModel.getAllCategories()
        .map { list -> list.filter { it.name != "All" && it.name != "Transfer" } }
        .flowOn(context = Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryTransactions = _selectedCategory
        .flatMapLatest {
            when (it) {
                Category() -> emptyFlow()
                else -> defaultEntryRepository.getEntriesByCategoryId(id = it.id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = emptyList()
        )

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }
}