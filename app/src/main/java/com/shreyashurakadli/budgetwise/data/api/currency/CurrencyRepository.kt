package com.shreyashurakadli.budgetwise.data.api.currency

import com.shreyashurakadli.budgetwise.data.dto.currency.CurrencyResponse
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    suspend fun getExchangeRates(): Flow<List<CurrencyResponse>>
}