package com.example.spend.data.room.currency

import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    suspend fun insert(currency: Currency)

    suspend fun update(currency: Currency)

    suspend fun delete(currency: Currency)

    suspend fun deleteAll()

    fun getAll(): Flow<List<Currency>>
}