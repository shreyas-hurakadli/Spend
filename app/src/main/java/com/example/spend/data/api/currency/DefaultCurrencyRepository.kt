package com.example.spend.data.api.currency

import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.dto.currency.CurrencyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DefaultCurrencyRepository @Inject constructor(
    private val apiService: CurrencyApiService,
    private val preferencesRepository: PreferencesRepository
) : CurrencyRepository {
    override suspend fun getExchangeRates(): Flow<List<CurrencyResponse>> = flow {
        try {
            val res = apiService.getCurrencyRates(baseCurrency = preferencesRepository.baseCurrency.first())
            if (res.isSuccessful) {
                res.body()?.let {
                    emit(
                        value = it.rates
                            .map { currencyRate ->
                                CurrencyResponse(
                                    name = currencyRate.key,
                                    rates = currencyRate.value
                                )
                            }
                    )
                }
            } else {
                emit(value = emptyList())
            }
        } catch (e: Exception) {
            emit(value = emptyList())
        }
    }.flowOn(context = Dispatchers.IO)
}