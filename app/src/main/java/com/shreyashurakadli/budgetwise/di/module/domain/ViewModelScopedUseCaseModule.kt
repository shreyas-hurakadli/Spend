package com.shreyashurakadli.budgetwise.di.module.domain

import com.shreyashurakadli.budgetwise.data.room.RoomDatabaseClass
import com.shreyashurakadli.budgetwise.data.room.account.AccountRepository
import com.shreyashurakadli.budgetwise.data.room.entry.EntryRepository
import com.shreyashurakadli.budgetwise.domain.account.DeleteAccount
import com.shreyashurakadli.budgetwise.domain.account.EditAccount
import com.shreyashurakadli.budgetwise.domain.entry.DeleteTransaction
import com.shreyashurakadli.budgetwise.domain.entry.EditTransaction
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
}