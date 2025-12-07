package com.example.spend.data.room.budget

import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    suspend fun insert(budget: Budget): Long

    suspend fun update(budget: Budget)

    suspend fun delete(budget: Budget)

    fun thereAreBudgets(): Flow<Boolean>

    fun getAllBudgets(): Flow<List<Budget>>
}