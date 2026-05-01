package com.shreyashurakadli.budgetwise.domain.settings

import androidx.room.withTransaction
import com.shreyashurakadli.budgetwise.data.room.RoomDatabaseClass
import com.shreyashurakadli.budgetwise.data.room.account.AccountRepository
import com.shreyashurakadli.budgetwise.data.room.budget.BudgetRepository
import com.shreyashurakadli.budgetwise.data.room.category.CategoryRepository
import com.shreyashurakadli.budgetwise.data.room.currency.CurrencyRepository
import com.shreyashurakadli.budgetwise.data.room.entry.EntryRepository
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