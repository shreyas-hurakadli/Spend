package com.example.spend.data.room.category

interface CategoryRepository {
    suspend fun insert(category: Category): Long

    suspend fun update(category: Category)

    suspend fun delete(category: Category)
}