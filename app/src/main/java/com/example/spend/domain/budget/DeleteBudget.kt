package com.example.spend.domain.budget

import androidx.room.withTransaction
import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.budget.Budget
import com.example.spend.data.room.budget.BudgetRepository
import javax.inject.Inject

class DeleteBudget @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(budget: Budget): Boolean = try {
        database.withTransaction {
            budgetRepository.delete(budget = budget)
        }
        true
    } catch (e: Exception) {
        false
    }
}