package com.example.spend.data.room.category

import androidx.compose.ui.graphics.Color
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Upsert
    suspend fun upsert(category: Category)

    @Query("DELETE FROM categories WHERE name != 'All'")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'categories'")
    suspend fun resetAutoIncrement()

    @Transaction
    suspend fun resetData() {
        deleteAll()
        upsert(
            Category(
                name = "All",
                isExpense = false,
                color = Color(-14634326),
                icon = null
            )
        )
        upsert(
            Category(
                name = "All",
                isExpense = true,
                color = Color(-14634326),
                icon = null
            )
        )
        upsert(
            Category(
                name = "Transfer",
                isExpense = true,
                color = Color(-14634326),
                icon = "transfer"
            )
        )
        upsert(
            Category(
                name = "Transfer",
                isExpense = false,
                color = Color(-14634326),
                icon = "transfer"
            )
        )
        upsert(
            Category(
                name = "Awards",
                isExpense = false,
                color = Color(-8921737),
                icon = "award"
            )
        )
        upsert(
            Category(
                name = "Coupons",
                isExpense = false,
                color = Color(-5323057),
                icon = "label"
            )
        )
        upsert(
            Category(
                name = "Deposit",
                isExpense = false,
                color = Color(-3429685),
                icon = "coin"
            )
        )
        upsert(
            Category(
                name = "Salary",
                isExpense = false,
                color = Color(-19641),
                icon = "coin"
            )
        )
        upsert(
            Category(
                name = "Car",
                isExpense = true,
                color = Color(-8921737),
                icon = "car"
            )
        )
        upsert(
            Category(
                name = "Entertainment",
                isExpense = true,
                color = Color(-5323057),
                icon = "movie"
            )
        )
        upsert(
            Category(
                name = "Food",
                isExpense = true,
                color = Color(-3429685),
                icon = "food"
            )
        )
        upsert(
            Category(
                name = "Health",
                isExpense = true,
                color = Color(-19641),
                icon = "hospital"
            )
        )
        upsert(
            Category(
                name = "Home",
                isExpense = true,
                color = Color(-5005643),
                icon = "home"
            )
        )
        upsert(
            Category(
                name = "Shopping",
                isExpense = true,
                color = Color(-38559),
                icon = "groceries"
            )
        )
        upsert(
            Category(
                name = "Sports",
                isExpense = true,
                color = Color(-38559),
                icon = "game"
            )
        )
        upsert(
            Category(
                name = "Transportation",
                isExpense = true,
                color = Color(-7357297),
                icon = "bus"
            )
        )
    }

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