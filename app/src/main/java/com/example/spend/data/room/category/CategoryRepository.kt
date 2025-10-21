package com.example.spend.data.room.category

import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun insert(category: Category): Long

    suspend fun update(category: Category)

    suspend fun delete(category: Category)

    fun getAllCategories(): Flow<List<Category>>

    fun getAllIncomeCategories(): Flow<List<Category>>

    fun getAllExpenseCategories(): Flow<List<Category>>
}