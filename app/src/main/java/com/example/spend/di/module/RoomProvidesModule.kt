package com.example.spend.di.module

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.spend.data.room.entry.EntryDao
import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.account.AccountDao
import com.example.spend.data.room.budget.BudgetDao
import com.example.spend.data.room.category.CategoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomProvidesModule {
    private val callback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.execSQL("INSERT INTO accounts (name, balance) VALUES ('All', 0.0)")
            db.execSQL("INSERT INTO categories (name, is_expense) VALUES ('All', 0)")
            db.execSQL("INSERT INTO categories (name, is_expense) VALUES ('All', 1)")
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            db.execSQL("INSERT INTO accounts (name, balance) VALUES ('All', 0.0)")
            db.execSQL("INSERT INTO categories (name, is_expense) VALUES ('All', 0)")
            db.execSQL("INSERT INTO categories (name, is_expense) VALUES ('All', 1)")
        }
    }
    @Provides
    @Singleton
    fun provideDatabaseInstance(@ApplicationContext context: Context): RoomDatabaseClass =
        Room.databaseBuilder(
            context,
            RoomDatabaseClass::class.java,
            "entries_database"
        )
            .fallbackToDestructiveMigration(dropAllTables = false)
            .addCallback(callback = callback)
            .build()

    @Provides
    @Singleton
    fun provideEntryDao(roomDatabaseClass: RoomDatabaseClass): EntryDao =
        roomDatabaseClass.entryDao()

    @Provides
    @Singleton
    fun provideAccountDao(roomDatabaseClass: RoomDatabaseClass): AccountDao =
        roomDatabaseClass.accountDao()

    @Provides
    @Singleton
    fun provideBudgetDao(roomDatabaseClass: RoomDatabaseClass): BudgetDao =
        roomDatabaseClass.budgetDao()

    @Provides
    @Singleton
    fun provideCategoryDao(roomDatabaseClass: RoomDatabaseClass): CategoryDao =
        roomDatabaseClass.categoryDao()
}