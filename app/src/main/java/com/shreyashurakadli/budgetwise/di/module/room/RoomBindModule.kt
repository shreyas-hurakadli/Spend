package com.shreyashurakadli.budgetwise.di.module.room

import com.shreyashurakadli.budgetwise.data.room.account.AccountRepository
import com.shreyashurakadli.budgetwise.data.room.account.DefaultAccountRepository
import com.shreyashurakadli.budgetwise.data.room.budget.BudgetRepository
import com.shreyashurakadli.budgetwise.data.room.budget.DefaultBudgetRepository
import com.shreyashurakadli.budgetwise.data.room.category.CategoryRepository
import com.shreyashurakadli.budgetwise.data.room.category.DefaultCategoryRepository
import com.shreyashurakadli.budgetwise.data.room.currency.CurrencyRepository
import com.shreyashurakadli.budgetwise.data.room.currency.DefaultCurrencyRepository
import com.shreyashurakadli.budgetwise.data.room.entry.DefaultRepository
import com.shreyashurakadli.budgetwise.data.room.entry.EntryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomBindModule {
    @Binds
    abstract fun bindEntryRepository(defaultRepository: DefaultRepository): EntryRepository

    @Binds
    abstract fun bindAccountRepository(defaultAccountRepository: DefaultAccountRepository): AccountRepository

    @Binds
    abstract fun bindCategoryRepository(defaultCategoryRepository: DefaultCategoryRepository): CategoryRepository

    @Binds
    abstract fun bindBudgetRepository(defaultBudgetRepository: DefaultBudgetRepository): BudgetRepository

    @Binds
    abstract fun bindCurrencyRepository(defaultCurrencyRepository: DefaultCurrencyRepository): CurrencyRepository
}