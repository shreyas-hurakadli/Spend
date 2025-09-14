package com.example.spend.data.room.budget

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: Budget): Long

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)
}