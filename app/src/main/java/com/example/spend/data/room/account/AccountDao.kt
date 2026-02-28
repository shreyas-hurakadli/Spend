package com.example.spend.data.room.account

import androidx.compose.ui.graphics.Color
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: Account): Long

    @Delete
    suspend fun delete(account: Account)

    @Update
    suspend fun update(account: Account)

    @Upsert
    suspend fun upsert(account: Account)

    @Query("DELETE FROM accounts WHERE name != 'All'")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'accounts'")
    suspend fun resetAutoIncrement()

    @Transaction
    suspend fun resetData() {
        deleteAll()
        upsert(Account(name = "All", balance = 0.0, color = Color(-14634326), icon = null))
        upsert(Account(name = "Cash", balance = 0.0, color = Color(-8921737), icon = "cash"))
        upsert(Account(name = "Card", balance = 0.0, color = Color(-5323057), icon = "card"))
        upsert(Account(name = "Savings", balance = 0.0, color = Color(-19641), icon = "piggybank"))
    }

    @Query("SELECT EXISTS (SELECT 1 FROM accounts WHERE name != 'All')")
    fun thereAreAccounts(): Flow<Boolean>

    @Query("SELECT * FROM accounts WHERE id = :id")
    fun getAccountById(id: Long): Flow<Account?>

    @Query("SELECT * FROM accounts LIMIT 1")
    fun getFirstAccount(): Flow<Account>

    @Query("SELECT * FROM accounts ORDER BY id ASC")
    fun getAllAccounts(): Flow<List<Account>>
}