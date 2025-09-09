package com.example.spend.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountDao
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryDao

@Database(entities = [Entry::class, Account::class], version = 2)
abstract class RoomDatabaseClass: RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun accountDao(): AccountDao
}