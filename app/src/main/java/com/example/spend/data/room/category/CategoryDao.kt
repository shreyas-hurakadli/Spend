package com.example.spend.data.room.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategory(id: Long): Flow<Category>

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE is_expense = 0")
    fun getAllIncomeCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE is_expense = 1")
    fun getAllExpenseCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    fun findCategoryById(id: Long): Flow<Category>

    @Query("SELECT * FROM categories WHERE name = :name AND is_expense = :isExpense")
    fun findCategoryByNameAndType(name: String, isExpense: Int): Flow<Category>
}