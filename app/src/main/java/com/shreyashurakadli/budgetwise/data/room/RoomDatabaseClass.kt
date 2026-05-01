package com.shreyashurakadli.budgetwise.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shreyashurakadli.budgetwise.data.room.account.Account
import com.shreyashurakadli.budgetwise.data.room.account.AccountDao
import com.shreyashurakadli.budgetwise.data.room.budget.Budget
import com.shreyashurakadli.budgetwise.data.room.budget.BudgetDao
import com.shreyashurakadli.budgetwise.data.room.category.Category
import com.shreyashurakadli.budgetwise.data.room.category.CategoryDao
import com.shreyashurakadli.budgetwise.data.room.converters.ColorConverter
import com.shreyashurakadli.budgetwise.data.room.currency.Currency
import com.shreyashurakadli.budgetwise.data.room.currency.CurrencyDao
import com.shreyashurakadli.budgetwise.data.room.entry.Entry
import com.shreyashurakadli.budgetwise.data.room.entry.EntryDao

@Database(entities = [Entry::class, Account::class, Budget::class, Category::class, Currency::class], version = 14)
@TypeConverters(ColorConverter::class)
abstract class RoomDatabaseClass : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun accountDao(): AccountDao

    abstract fun budgetDao(): BudgetDao

    abstract fun categoryDao(): CategoryDao

    abstract fun currencyDao(): CurrencyDao
}