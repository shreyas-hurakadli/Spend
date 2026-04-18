package com.example.spend.di.module.domain

import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.budget.BudgetRepository
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.currency.CurrencyRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.domain.account.AddAccount
import com.example.spend.domain.budget.DeleteBudget
import com.example.spend.domain.budget.EditBudget
import com.example.spend.domain.category.AddCategory
import com.example.spend.domain.entry.AddEntryToDb
import com.example.spend.domain.settings.ResetData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideAddEntryToDb(
        entryRepository: EntryRepository,
        accountRepository: AccountRepository,
        database: RoomDatabaseClass
    ): AddEntryToDb = AddEntryToDb(
        entryRepository = entryRepository,
        accountRepository = accountRepository,
        database = database
    )

    @Provides
    fun provideAddCategory(
        categoryRepository: CategoryRepository,
        database: RoomDatabaseClass
    ): AddCategory = AddCategory(
        categoryRepository = categoryRepository,
        database = database
    )

    @Provides
    fun provideAddAccount(
        accountRepository: AccountRepository,
        database: RoomDatabaseClass
    ): AddAccount = AddAccount(
        accountRepository = accountRepository,
        database = database
    )

    @Provides
    fun provideResetData(
        entryRepository: EntryRepository,
        accountRepository: AccountRepository,
        categoryRepository: CategoryRepository,
        budgetRepository: BudgetRepository,
        currencyRepository: CurrencyRepository,
        database: RoomDatabaseClass
    ): ResetData = ResetData(
        entryRepository = entryRepository,
        accountRepository = accountRepository,
        categoryRepository = categoryRepository,
        budgetRepository = budgetRepository,
        currencyRepository = currencyRepository,
        database = database
    )

    @Provides
    fun provideDeleteBudget(
        budgetRepository: BudgetRepository,
        database: RoomDatabaseClass
    ): DeleteBudget = DeleteBudget(
        budgetRepository = budgetRepository,
        database = database
    )

    @Provides
    fun provideEditBudget(
        budgetRepository: BudgetRepository,
        database: RoomDatabaseClass
    ): EditBudget = EditBudget(
        budgetRepository = budgetRepository,
        database = database
    )
}