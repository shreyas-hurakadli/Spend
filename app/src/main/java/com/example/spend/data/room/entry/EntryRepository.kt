package com.example.spend.data.room.entry

import com.example.spend.data.dto.CategoryAmount
import com.example.spend.data.dto.EntryCategory
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

    fun getExpenseByCategory(from: Long, to: Long): Flow<List<CategoryAmount>>

    fun getIncomeByCategory(from: Long, to: Long): Flow<List<CategoryAmount>>

    fun areEntriesPresent(): Flow<Boolean>

    fun getAllExpenseAmount(): Flow<List<Double>>

    fun getAllIncomeAmount(): Flow<List<Double>>

    fun getIncomeByTime(from: Long, to: Long): Flow<Map<Long, Double>>

    fun getExpenseByTime(from: Long, to: Long): Flow<Map<Long, Double>>

    fun getEntryIconAndColor(limit: Long = Long.MAX_VALUE): Flow<List<EntryCategory>>

    fun getExpenseByBudgetConstraintsUsingAccount(
        accountId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<Double>

    fun getExpenseByBudgetConstraintsUsingCategory(
        categoryId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<Double>

    fun getExpenseByBudgetConstraintsUsingOnlyTime(
        startTime: Long,
        endTime: Long
    ): Flow<Double>

    fun getExpenseByBudgetConstraints(
        accountId: Long,
        categoryId: Long,
        startTime: Long,
        endTime: Long,
    ): Flow<Double>

    fun getEntriesByBudgetConstraintsUsingAccount(
        accountId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<Entry>>

    fun getEntriesByBudgetConstraintsUsingCategory(
        categoryId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<Entry>>

    fun getEntriesByBudgetConstraintsUsingOnlyTime(
        startTime: Long,
        endTime: Long
    ): Flow<List<Entry>>

    fun getEntriesByBudgetConstraints(
        accountId: Long,
        categoryId: Long,
        startTime: Long,
        endTime: Long,
    ): Flow<List<Entry>>
}