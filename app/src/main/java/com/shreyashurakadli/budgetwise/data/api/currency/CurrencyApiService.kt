package com.shreyashurakadli.budgetwise.data.api.currency

import androidx.annotation.Keep
import com.shreyashurakadli.budgetwise.data.dto.currency.CurrencyApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

@Keep
interface CurrencyApiService {
    @GET(value = "v1/latest")
    suspend fun getCurrencyRates(@Query(value = "base") baseCurrency: String): Response<CurrencyApiResponse>
}