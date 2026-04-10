package com.example.spend.di.module.domain

import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.domain.DeleteTransaction
import com.example.spend.domain.EditTransaction
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
    ) = DeleteTransaction(
        entryRepository = entryRepository,
        accountRepository = accountRepository,
        database = database
    )
    
    @Provides
    fun provideEditTransaction(
        entryRepository: EntryRepository,
        accountRepository: AccountRepository,
        database: RoomDatabaseClass
    ) = EditTransaction(
        entryRepository = entryRepository,
        accountRepository = accountRepository,
        database = database
    )
}