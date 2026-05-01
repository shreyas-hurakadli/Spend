package com.shreyashurakadli.budgetwise.domain.category

import androidx.room.withTransaction
import com.shreyashurakadli.budgetwise.data.room.RoomDatabaseClass
import com.shreyashurakadli.budgetwise.data.room.category.Category
import com.shreyashurakadli.budgetwise.data.room.category.CategoryRepository
import javax.inject.Inject

class EditCategory @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(
        editedCategory: Category
    ): Boolean =
        try {
            database.withTransaction {
                categoryRepository.update(category = editedCategory.copy(name = editedCategory.name.trim()))
            }
            true
        } catch (e: Exception) {
            false
        }
}