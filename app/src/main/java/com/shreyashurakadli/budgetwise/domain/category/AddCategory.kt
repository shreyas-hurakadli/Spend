package com.shreyashurakadli.budgetwise.domain.category

import androidx.room.withTransaction
import com.shreyashurakadli.budgetwise.data.room.RoomDatabaseClass
import com.shreyashurakadli.budgetwise.data.room.category.Category
import com.shreyashurakadli.budgetwise.data.room.category.CategoryRepository
import jakarta.inject.Inject

class AddCategory @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(
        category: Category
    ): Boolean = try {
        database.withTransaction {
            categoryRepository.insert(category = category)
        }
        true
    } catch (e: Exception) {
        false
    }
}