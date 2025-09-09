package com.example.spend.di.module

import android.content.Context
import androidx.room.Room
import com.example.spend.data.room.entry.EntryDao
import com.example.spend.data.room.RoomDatabaseClass
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
    fun provideDatabaseInstance(@ApplicationContext context: Context): RoomDatabaseClass =
        Room.databaseBuilder(
            context,
            RoomDatabaseClass::class.java,
            "entries_database"
        ).build()

    @Provides
    @Singleton
    fun provideEntryDao(roomDatabaseClass: RoomDatabaseClass): EntryDao =
        roomDatabaseClass.entryDao()
}