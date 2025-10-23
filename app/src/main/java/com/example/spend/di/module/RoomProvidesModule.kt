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
            db.execSQL("INSERT INTO accounts (name, balance) VALUES ('Cash', 0.0)")
            db.execSQL("INSERT INTO accounts (name, balance) VALUES ('Card', 0.0)")
            db.execSQL("INSERT INTO accounts (name, balance) VALUES ('Savings', 0.0)")

            db.execSQL("INSERT INTO categories (name, is_expense, color) VALUES ('All', 0, -1)")
            db.execSQL("INSERT INTO categories (name, is_expense, color) VALUES ('All', 1, -1)")
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Awards', 0, -8921737, 'award')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Coupons', 0, -5323057, 'label')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Deposit', 0, -3429685, 'coin')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Salary', 0, -19641, 'coin')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Car', 1, -8921737, 'car')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Entertainment', 1, -5323057, 'movie')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Food', 1, -3429685, 'food')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Health', 1, -19641, 'hospital')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Home', 1, -5005643, 'home')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Shopping', 1, -38559, 'groceries')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Sports', 1, -38559, 'game')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Transportation', 1, -7357297, 'bus')");
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            db.execSQL("INSERT INTO accounts (name, balance) VALUES ('All', 0.0)")
            db.execSQL("INSERT INTO accounts (name, balance) VALUES ('Cash', 0.0)")
            db.execSQL("INSERT INTO accounts (name, balance) VALUES ('Card', 0.0)")
            db.execSQL("INSERT INTO accounts (name, balance) VALUES ('Savings', 0.0)")

            db.execSQL("INSERT INTO categories (name, is_expense, color) VALUES ('All', 0, -1)")
            db.execSQL("INSERT INTO categories (name, is_expense, color) VALUES ('All', 1, -1)")
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Awards', 0, -8921737, 'award')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Coupons', 0, -5323057, 'label')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Deposit', 0, -3429685, 'coin')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Salary', 0, -19641, 'coin')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Car', 1, -8921737, 'car')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Entertainment', 1, -5323057, 'movie')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Food', 1, -3429685, 'food')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Health', 1, -19641, 'hospital')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Home', 1, -5005643, 'home')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Shopping', 1, -38559, 'groceries')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Sports', 1, -38559, 'game')");
            db.execSQL("INSERT INTO categories (name, is_expense, color, icon) VALUES ('Transportation', 1, -7357297, 'bus')");
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