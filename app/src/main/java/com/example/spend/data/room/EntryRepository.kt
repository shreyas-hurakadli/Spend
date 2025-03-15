package com.example.spend.data.room

import kotlinx.coroutines.flow.Flow

interface EntryRepository {
    suspend fun insert(entry: Entry)

    suspend fun update(entry: Entry)

    suspend fun delete(entry: Entry)

    fun getAllEntries(): Flow<List<Entry>>

    fun getRecentEntries(): Flow<List<Entry>>

    suspend fun deleteAll()

    suspend fun resetAutoincrement()

    fun getExpense(from: Long): Flow<Int>
}