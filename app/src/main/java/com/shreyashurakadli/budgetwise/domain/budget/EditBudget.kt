package com.shreyashurakadli.budgetwise.domain.budget

import androidx.room.withTransaction
import com.shreyashurakadli.budgetwise.data.room.RoomDatabaseClass
import com.shreyashurakadli.budgetwise.data.room.budget.Budget
import com.shreyashurakadli.budgetwise.data.room.budget.BudgetRepository
import javax.inject.Inject

class EditBudget @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(
        editedBudget: Budget
    ): Boolean =
        try {
            database.withTransaction {
                budgetRepository.update(budget = editedBudget)
            }
            true
        } catch (e: Exception) {
            false
        }
}