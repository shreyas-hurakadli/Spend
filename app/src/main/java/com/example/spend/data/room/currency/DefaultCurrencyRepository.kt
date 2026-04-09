package com.example.spend.data.room.currency

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class DefaultCurrencyRepository @Inject constructor(
    private val dao: CurrencyDao,
) : CurrencyRepository {
    override suspend fun insert(currency: Currency) = dao.insert(currency)

    override suspend fun update(currency: Currency) = dao.update(currency)

    override suspend fun delete(currency: Currency) = dao.delete(currency)

    override suspend fun deleteAll() {
        dao.deleteAll()
        dao.resetAutoIncrement()
    }

    override fun getAll(): Flow<List<Currency>> = dao.getAll()

    override fun getByCode(code: String): Flow<Currency> = dao.getByCode(code = code)
}