package com.example.spend.di.module.domain

import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.budget.BudgetRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.domain.account.DeleteAccount
import com.example.spend.domain.account.EditAccount
import com.example.spend.domain.budget.EditBudget
import com.example.spend.domain.entry.DeleteTransaction
import com.example.spend.domain.entry.EditTransaction
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelScopedUseCaseModule {
    @Provides
    fun provideDeleteTransaction(
        entryRepository: EntryRepository,
        accountRepository: AccountRepository,
        database: RoomDatabaseClass
    ): DeleteTransaction = DeleteTransaction(
        entryRepository = entryRepository,
        accountRepository = accountRepository,
        database = database
    )

    @Provides
    fun provideEditTransaction(
        entryRepository: EntryRepository,
        accountRepository: AccountRepository,
        database: RoomDatabaseClass
    ): EditTransaction = EditTransaction(
        entryRepository = entryRepository,
        accountRepository = accountRepository,
        database = database
    )

    @Provides
    fun provideDeleteAccount(
        accountRepository: AccountRepository,
        database: RoomDatabaseClass
    ): DeleteAccount = DeleteAccount(
        accountRepository = accountRepository,
        database = database
    )

    @Provides
    fun provideEditAccount(
        accountRepository: AccountRepository,
        database: RoomDatabaseClass
    ): EditAccount = EditAccount(
        accountRepository = accountRepository,
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