package com.example.spend.data.room

import com.example.spend.model.TagBillSum
import kotlinx.coroutines.flow.Flow

interface EntryRepository {
    suspend fun insert(entry: Entry)

    suspend fun update(entry: Entry)

    suspend fun delete(entry: Entry)

    fun getAllEntries(): Flow<List<Entry>>

    fun getRecentEntries(): Flow<List<Entry>>

    suspend fun deleteAll()

    suspend fun resetAutoincrement()

    fun getExpense(from: Long): Flow<Double>

    fun getIncome(from: Long): Flow<Double>

    fun getExpenseByCategory(): Flow<Map<String, Double>>

    fun getIncomeByCategory(): Flow<Map<String, Double>>

    fun areEntriesPresent(): Flow<Boolean>

    fun getAllExpenseAmount(): Flow<List<Double>>

    fun getAllIncomeAmount(): Flow<List<Double>>
}