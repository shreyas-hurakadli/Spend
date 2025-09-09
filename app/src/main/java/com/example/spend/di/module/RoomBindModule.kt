package com.example.spend.di.module

import com.example.spend.data.room.entry.DefaultRepository
import com.example.spend.data.room.entry.EntryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomBindModule {
    @Binds
    abstract fun bindEntryRepository(defaultRepository: DefaultRepository): EntryRepository
}