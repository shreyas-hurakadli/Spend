package com.example.spend.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Entry::class], version = 1)
abstract class EntryDatabase: RoomDatabase() {
    abstract fun entryDao(): EntryDao

    companion object {
        @Volatile
        private var Instance: EntryDatabase? = null

        fun getDatabase(context: Context): EntryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, EntryDatabase::class.java, "entries_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}