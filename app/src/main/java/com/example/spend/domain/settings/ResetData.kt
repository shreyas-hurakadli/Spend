package com.example.spend.domain.settings

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
    private val currencyRepository: CurrencyRepository
) {
    suspend operator fun invoke(): Boolean =
        try {
            entryRepository.deleteAll()
            accountRepository.resetData()
            categoryRepository.resetData()
            budgetRepository.deleteAll()
            currencyRepository.deleteAll()
            true
        } catch (e: Exception) {
            false
        }
}