package com.example.spend.data.room.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)
}