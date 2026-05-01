package com.shreyashurakadli.budgetwise.data.workmanager.currency

interface CurrencyApiRepository {
    fun getExchangeRateNow()
    fun scheduleExchangeRateFetch()
}