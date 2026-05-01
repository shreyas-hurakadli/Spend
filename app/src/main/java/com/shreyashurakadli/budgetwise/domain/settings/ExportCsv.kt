package com.shreyashurakadli.budgetwise.domain.settings

import android.net.Uri
import androidx.room.withTransaction
import com.shreyashurakadli.budgetwise.data.local.file.CsvExportableRepository
import com.shreyashurakadli.budgetwise.data.room.RoomDatabaseClass
import com.shreyashurakadli.budgetwise.data.room.account.Account
import com.shreyashurakadli.budgetwise.data.room.account.AccountRepository
import com.shreyashurakadli.budgetwise.data.room.budget.Budget
import com.shreyashurakadli.budgetwise.data.room.budget.BudgetRepository
import com.shreyashurakadli.budgetwise.data.room.category.Category
import com.shreyashurakadli.budgetwise.data.room.category.CategoryRepository
import com.shreyashurakadli.budgetwise.data.room.entry.Entry
import com.shreyashurakadli.budgetwise.data.room.entry.EntryRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExportCsv @Inject constructor(
    private val entryRepository: EntryRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
    private val budgetRepository: BudgetRepository,
    private val csvExportableRepository: CsvExportableRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(
        directory: Uri
    ): Boolean =
        try {
             database.withTransaction {
                val entries = entryRepository.getAllEntries().first()
                csvExportableRepository.writeFile(
                    parentDirectory = directory,
                    fileName = "entries.csv",
                    header = Entry.HEADER,
                    data = entries
                )
                val categories = categoryRepository.getAllCategories().first()
                csvExportableRepository.writeFile(
                    parentDirectory = directory,
                    fileName = "categories.csv",
                    header = Category.HEADER,
                    data = categories
                )
                val accounts = accountRepository.getAllAccounts().first()
                csvExportableRepository.writeFile(
                    parentDirectory = directory,
                    fileName = "accounts.csv",
                    header = Account.HEADER,
                    data = accounts
                )
                val budgets = budgetRepository.getAllBudgets().first()
                csvExportableRepository.writeFile(
                    parentDirectory = directory,
                    fileName = "budgets.csv",
                    header = Budget.HEADER,
                    data = budgets
                )
            }
            true
        } catch (e: Exception) {
            false
        }
}