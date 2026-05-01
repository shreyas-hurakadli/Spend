package com.shreyashurakadli.budgetwise.di.module.domain

import android.content.Context
import com.shreyashurakadli.budgetwise.data.room.RoomDatabaseClass
import com.shreyashurakadli.budgetwise.data.room.account.AccountRepository
import com.shreyashurakadli.budgetwise.data.room.budget.BudgetRepository
import com.shreyashurakadli.budgetwise.data.room.category.CategoryRepository
import com.shreyashurakadli.budgetwise.data.room.currency.CurrencyRepository
import com.shreyashurakadli.budgetwise.data.room.entry.EntryRepository
import com.shreyashurakadli.budgetwise.domain.account.AddAccount
import com.shreyashurakadli.budgetwise.domain.budget.DeleteBudget
import com.shreyashurakadli.budgetwise.domain.budget.EditBudget
import com.shreyashurakadli.budgetwise.domain.category.AddCategory
import com.shreyashurakadli.budgetwise.domain.entry.AddEntryToDb
import com.shreyashurakadli.budgetwise.domain.settings.RedirectToUrl
import com.shreyashurakadli.budgetwise.domain.settings.ResetData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    fun provideRedirectUrl(
        @ApplicationContext context: Context
    ): RedirectToUrl = RedirectToUrl(context = context)
}