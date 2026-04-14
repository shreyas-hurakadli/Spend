package com.example.spend.domain.category

import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import jakarta.inject.Inject

class AddCategory @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(
        category: Category
    ): Boolean = try {
        categoryRepository.insert(category = category)
        true
    } catch (e: Exception) {
        false
    }
}