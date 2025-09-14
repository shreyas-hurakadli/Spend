package com.example.spend.data.room.budget

interface BudgetRepository {
    suspend fun insert(budget: Budget): Long

    suspend fun update(budget: Budget)

    suspend fun delete(budget: Budget)
}