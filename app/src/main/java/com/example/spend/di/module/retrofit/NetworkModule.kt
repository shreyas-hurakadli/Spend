package com.example.spend.di.module.retrofit

import com.example.spend.data.api.currency.CurrencyApiService
import com.example.spend.ui.data.CURRENCY_API_URL
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    fun provideUrl(): String = CURRENCY_API_URL

    @Provides
    @Singleton
    fun provideRetrofit(
        json: Json,
        url: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): CurrencyApiService = retrofit.create(
        CurrencyApiService::class.java
    )
}