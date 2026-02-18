package com.example.spend.data.room.currency

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Insert
    suspend fun insert(currency: Currency)

    @Delete
    suspend fun delete(currency: Currency)

    @Update
    suspend fun update(currency: Currency)

    @Query("DELETE FROM currencies")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'currencies'")
    suspend fun resetAutoIncrement()

    @Query(value = "SELECT * FROM currencies")
    fun getAll(): Flow<List<Currency>>
}