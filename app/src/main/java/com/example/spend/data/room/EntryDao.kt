package com.example.spend.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.Query
import androidx.room.Update
import com.example.spend.model.TagBillSum
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert
    suspend fun insert(entry: Entry)

    @Update
    suspend fun update(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)

    @Query("SELECT * FROM entries ORDER BY epochSeconds DESC")
    fun getAllEntries(): Flow<List<Entry>>

    @Query("SELECT * FROM entries ORDER BY id DESC LIMIT 4")
    fun getRecentEntries(): Flow<List<Entry>>

    @Query("DELETE FROM entries")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = :tableName")
    suspend fun resetAutoIncrement(tableName: String)

    @Query("SELECT SUM(amount) FROM entries WHERE isExpense = 1 AND epochSeconds >= :from")
    fun getExpense(from: Long): Flow<Double>

    @Query("SELECT SUM(amount) FROM entries WHERE isExpense = 0 AND epochSeconds >= :from")
    fun getIncome(from: Long): Flow<Double>

    @Query("SELECT category, SUM(amount) AS amount FROM entries WHERE isExpense = 1 GROUP BY category")
    fun getExpenseByCategory(): Flow<Map<@MapColumn("category") String, @MapColumn("amount") Double>>

    @Query("SELECT category, SUM(amount) AS amount FROM entries WHERE isExpense = 0 GROUP BY category")
    fun getIncomeByCategory(): Flow<Map<@MapColumn("category") String, @MapColumn("amount") Double>>

    @Query("SELECT EXISTS (SELECT 1 FROM entries WHERE id IS NOT NULL)")
    fun areEntriesPresent(): Flow<Boolean>

    @Query("SELECT amount FROM entries WHERE isExpense = 1")
    fun getAllExpenseAmount(): Flow<List<Double>>

    @Query("SELECT amount FROM entries WHERE isExpense = 0")
    fun getAllIncomeAmount(): Flow<List<Double>>
}