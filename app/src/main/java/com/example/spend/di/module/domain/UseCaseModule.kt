package com.example.spend.di.module.domain

import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.domain.entry.AddEntryToDb
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
        roomDatabase: RoomDatabaseClass
    ): AddEntryToDb = AddEntryToDb(
        entryRepository = entryRepository,
        accountRepository = accountRepository,
        database = roomDatabase
    )
}