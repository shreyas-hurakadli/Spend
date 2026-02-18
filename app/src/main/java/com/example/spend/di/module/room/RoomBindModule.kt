package com.example.spend.di.module.room

import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.account.DefaultAccountRepository
import com.example.spend.data.room.budget.BudgetRepository
import com.example.spend.data.room.budget.DefaultBudgetRepository
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.category.DefaultCategoryRepository
import com.example.spend.data.room.entry.DefaultRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.data.workmanager.budget.BudgetNotificationRepository
import com.example.spend.data.workmanager.budget.DefaultBudgetNotificationRepository
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
    abstract fun bindBudgetNotificationRepository(defaultBudgetNotificationRepository: DefaultBudgetNotificationRepository): BudgetNotificationRepository
}