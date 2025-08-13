package com.example.spend.di.module

import android.content.Context
import androidx.room.Room
import com.example.spend.data.room.DefaultRepository
import com.example.spend.data.room.EntryDao
import com.example.spend.data.room.EntryDatabase
import com.example.spend.data.room.EntryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomProvidesModule {
    @Provides
    @Singleton
    fun provideDatabaseInstance(@ApplicationContext context: Context): EntryDatabase =
        Room.databaseBuilder(
            context,
            EntryDatabase::class.java,
            "entries_database"
        ).build()

    @Provides
    @Singleton
    fun provideEntryDao(entryDatabase: EntryDatabase): EntryDao =
        entryDatabase.entryDao()
}