package com.example.spend.di.module.domain

import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.category.CategoryRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.domain.account.AddAccount
import com.example.spend.domain.category.AddCategory
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
}