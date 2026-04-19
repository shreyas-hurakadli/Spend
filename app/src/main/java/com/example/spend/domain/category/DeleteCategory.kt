package com.example.spend.domain.category

import android.util.Log
import androidx.room.withTransaction
import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryRepository
import javax.inject.Inject

class DeleteCategory @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(category: Category): Boolean = try {
        Log.d("DeleteCategory", category.toString())
        database.withTransaction {
            categoryRepository.delete(category = category)
        }
        true
    } catch (e: Exception) {
        false
    }
}