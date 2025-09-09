package com.example.spend.data.room.entry

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultRepository @Inject constructor(private val entryDao: EntryDao) : EntryRepository {
    override suspend fun insert(entry: Entry) = entryDao.insert(entry)

    override suspend fun update(entry: Entry) = entryDao.update(entry)

    override suspend fun delete(entry: Entry) = entryDao.delete(entry)

    override fun getAllEntries(): Flow<List<Entry>> = entryDao.getAllEntries()

    override fun getRecentEntries(): Flow<List<Entry>> = entryDao.getRecentEntries()

    override suspend fun deleteAll() = entryDao.deleteAll()

    override suspend fun resetAutoincrement() = entryDao.resetAutoIncrement("entries")

    override fun getExpense(from: Long): Flow<Double> = entryDao.getExpense(from)

    override fun getIncome(from: Long): Flow<Double> = entryDao.getIncome(from)

    override fun getExpenseByCategory(from: Long, to: Long): Flow<Map<String, Double>> =
        entryDao.getExpenseByCategory(from, to)

    override fun getIncomeByCategory(from: Long, to: Long): Flow<Map<String, Double>> =
        entryDao.getIncomeByCategory(from, to)

    override fun areEntriesPresent(): Flow<Boolean> = entryDao.areEntriesPresent()

    override fun getAllExpenseAmount(): Flow<List<Double>> = entryDao.getAllExpenseAmount()

    override fun getAllIncomeAmount(): Flow<List<Double>> = entryDao.getAllIncomeAmount()

    override fun getIncomeByTime(from: Long, to: Long): Flow<Map<Long, Double>> =
        entryDao.getIncomeByTime(from, to)

    override fun getExpenseByTime(from: Long, to: Long): Flow<Map<Long, Double>> =
        entryDao.getExpenseByTime(from, to)
}