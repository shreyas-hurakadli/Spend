package com.example.spend.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert
    suspend fun insert(entry: Entry)

    @Update
    suspend fun update(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)

    @Query("SELECT * FROM entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<Entry>>

    @Query("SELECT * FROM entries ORDER BY id DESC LIMIT 4")
    fun getRecentEntries(): Flow<List<Entry>>

    @Query("DELETE FROM entries")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = :tableName")
    suspend fun resetAutoIncrement(tableName: String)

    @Query("SELECT SUM(bill) FROM entries WHERE date >= :from")
    fun getExpense(from: Long): Flow<Int>

    // @Query("SELECT tag, SUM(bill) FROM entries GROUP BY tag")
    // fun getTagList(): Flow<List<Entry>>
}