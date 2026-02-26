package com.example.spend.data.workmanager.currency

interface CurrencyApiRepository {
    fun getExchangeRateNow()
    fun scheduleExchangeRateFetch()
}