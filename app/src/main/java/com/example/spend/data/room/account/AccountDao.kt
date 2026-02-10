package com.example.spend.data.room.account

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: Account): Long

    @Delete
    suspend fun delete(account: Account)

    @Update
    suspend fun update(account: Account)

    @Query("SELECT EXISTS (SELECT 1 FROM accounts WHERE id IS NOT NULL)")
    fun thereAreAccounts(): Flow<Boolean>

    @Query("SELECT * FROM accounts WHERE id = :id")
    fun getAccountById(id: Long): Flow<Account?>

    @Query("SELECT * FROM accounts LIMIT 1")
    fun getFirstAccount(): Flow<Account>

    @Query("SELECT * FROM accounts ORDER BY id ASC")
    fun getAllAccounts(): Flow<List<Account>>
}