package com.shreyashurakadli.budgetwise.data.workmanager.currency

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shreyashurakadli.budgetwise.data.api.currency.CurrencyRepository
import com.shreyashurakadli.budgetwise.data.datastore.config.PreferencesRepository
import com.shreyashurakadli.budgetwise.data.dto.currency.CurrencyResponse
import com.shreyashurakadli.budgetwise.data.room.currency.Currency
import kotlinx.coroutines.flow.first

class CurrencyWorker(
    context: Context,
    params: WorkerParameters,
    private val defaultCurrencyRepository: CurrencyRepository,
    private val dbCurrencyRepository: com.shreyashurakadli.budgetwise.data.room.currency.CurrencyRepository,
    private val defaultPreferencesRepository: PreferencesRepository
) : CoroutineWorker(appContext = context, params = params) {
    override suspend fun doWork(): Result {
        return try {
            val exchangeRates = getExchangeRates()
            if (exchangeRates.isEmpty()) return Result.retry()
            insertExchangeRatesToDb(exchangeRates)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun getExchangeRates(): List<CurrencyResponse> =
        defaultCurrencyRepository.getExchangeRates().first()

    private suspend fun insertExchangeRatesToDb(exchangeRates: List<CurrencyResponse>) {
        val rates = exchangeRates.map {
            Currency(
                name = it.name,
                rate = it.rates
            )
        }
        dbCurrencyRepository.deleteAll()
        rates.forEach { currency ->
            dbCurrencyRepository.insert(currency)
        }
        val baseCurrency = defaultPreferencesRepository.baseCurrency.first()
        dbCurrencyRepository.insert(Currency(name = baseCurrency, rate = 1.00))
    }
}