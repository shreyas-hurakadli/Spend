package com.example.spend.domain.settings

import androidx.room.withTransaction
import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.budget.BudgetRepository
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.currency.CurrencyRepository
import com.example.spend.data.room.entry.EntryRepository
import jakarta.inject.Inject

class ResetData @Inject constructor(
    private val entryRepository: EntryRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val currencyRepository: CurrencyRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(): Boolean =
        try {
            database.withTransaction {
                entryRepository.deleteAll()
                accountRepository.resetData()
                categoryRepository.resetData()
                budgetRepository.deleteAll()
                currencyRepository.deleteAll()
            }
            true
        } catch (e: Exception) {
            false
        }
}