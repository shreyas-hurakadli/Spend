package com.example.spend.domain.category

import androidx.room.withTransaction
import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
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