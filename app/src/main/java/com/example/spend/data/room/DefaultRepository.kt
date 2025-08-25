package com.example.spend.data.room

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

    override fun getExpenseByCategory(): Flow<Map<String, Double>> = entryDao.getExpenseByCategory()

    override fun getIncomeByCategory(): Flow<Map<String, Double>> = entryDao.getIncomeByCategory()

    override fun areEntriesPresent(): Flow<Boolean> = entryDao.areEntriesPresent()

    override fun getAllExpenseAmount(): Flow<List<Double>> = entryDao.getAllExpenseAmount()

    override fun getAllIncomeAmount(): Flow<List<Double>> = entryDao.getAllIncomeAmount()
}