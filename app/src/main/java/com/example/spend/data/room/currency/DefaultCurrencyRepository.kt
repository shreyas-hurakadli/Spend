package com.example.spend.data.room.currency

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DefaultCurrencyRepository @Inject constructor(
    private val dao: CurrencyDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CurrencyRepository {
    override suspend fun insert(currency: Currency) = withContext(context = dispatcher) {
        dao.insert(currency)
    }

    override suspend fun update(currency: Currency) = withContext(context = dispatcher) {
        dao.update(currency)
    }

    override suspend fun delete(currency: Currency) = withContext(context = dispatcher) {
        dao.delete(currency)
    }

    override suspend fun deleteAll() = withContext(context = dispatcher) {
        dao.deleteAll()
        dao.resetAutoIncrement()
    }

    override fun getAll(): Flow<List<Currency>> = dao.getAll()
}