package com.example.spend.data.workmanager.currency

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.spend.data.api.currency.CurrencyRepository
import com.example.spend.data.dto.currency.CurrencyResponse
import com.example.spend.data.room.currency.Currency
import com.example.spend.toTwoDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class CurrencyWorker(
    context: Context,
    params: WorkerParameters,
    private val defaultCurrencyRepository: CurrencyRepository,
    private val dbCurrencyRepository: com.example.spend.data.room.currency.CurrencyRepository
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
        withContext(context = Dispatchers.IO) {
            dbCurrencyRepository.deleteAll()
            rates.forEach { currency ->
                dbCurrencyRepository.insert(currency)
            }
        }
    }
}