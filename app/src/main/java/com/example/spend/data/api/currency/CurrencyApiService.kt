package com.example.spend.data.api.currency

import com.example.spend.data.dto.currency.CurrencyApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET(value = "v1/latest")
    suspend fun getCurrencyRates(@Query(value = "base") baseCurrency: String): Response<CurrencyApiResponse>
}