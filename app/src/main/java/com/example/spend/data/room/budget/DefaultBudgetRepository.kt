package com.example.spend.data.room.budget

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultBudgetRepository @Inject constructor(
    private val dao: BudgetDao
): BudgetRepository {
    override suspend fun insert(budget: Budget) = dao.insert(budget)

    override suspend fun update(budget: Budget) = dao.update(budget)

    override suspend fun delete(budget: Budget) = dao.delete(budget)

    override fun getAllBudgets(): Flow<List<Budget>> = dao.getAllBudgets()

    override fun thereAreBudgets(): Flow<Boolean> = dao.thereAreNoBudgets()
}