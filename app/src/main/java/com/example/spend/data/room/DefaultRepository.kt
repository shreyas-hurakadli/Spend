package com.example.spend.data.room

import kotlinx.coroutines.flow.Flow

class DefaultRepository(private val entryDao: EntryDao): EntryRepository {
    override suspend fun insert(entry: Entry) = entryDao.insert(entry)

    override suspend fun update(entry: Entry) = entryDao.update(entry)

    override suspend fun delete(entry: Entry) = entryDao.delete(entry)

    override fun getAllEntries(): Flow<List<Entry>> = entryDao.getAllEntries()

    override fun getRecentEntries(): Flow<List<Entry>> = entryDao.getRecentEntries()

    override suspend fun deleteAll() = entryDao.deleteAll()

    override suspend fun resetAutoincrement() = entryDao.resetAutoIncrement("entries")

    override fun getExpense(from: Long): Flow<Int> = entryDao.getExpense(from)

    // override fun getTagList(): Flow<List<Entry>> = entryDao.getTagList()
}