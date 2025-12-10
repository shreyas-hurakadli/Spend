package com.example.spend.data.room.category

import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun insert(category: Category): Long

    suspend fun update(category: Category)

    suspend fun delete(category: Category)

    fun getCategory(id: Long): Flow<Category>

    fun getAllCategories(): Flow<List<Category>>

    fun getAllIncomeCategories(): Flow<List<Category>>

    fun getAllExpenseCategories(): Flow<List<Category>>

    fun findCategoryById(id: Long): Flow<Category>

    fun findCategoryByNameAndId(name: String, isExpense: Boolean): Flow<Category>
}