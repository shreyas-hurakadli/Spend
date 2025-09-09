package com.example.spend.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryDao

@Database(entities = [Entry::class], version = 1)
abstract class RoomDatabaseClass: RoomDatabase() {
    abstract fun entryDao(): EntryDao
}