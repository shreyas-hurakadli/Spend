package com.example.spend.data.api.currency

import com.example.spend.data.dto.currency.CurrencyResponse
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    suspend fun getExchangeRates(): Flow<List<CurrencyResponse>>
}