package com.example.spend.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountDao
import com.example.spend.data.room.budget.Budget
import com.example.spend.data.room.budget.BudgetDao
import com.example.spend.data.room.category.Category
import com.example.spend.data.room.category.CategoryDao
import com.example.spend.data.room.converters.ColorConverter
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryDao

@Database(entities = [Entry::class, Account::class, Budget::class, Category::class], version = 8)
@TypeConverters(ColorConverter::class)
abstract class RoomDatabaseClass : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun accountDao(): AccountDao

    abstract fun budgetDao(): BudgetDao

    abstract fun categoryDao(): CategoryDao
}