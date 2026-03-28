package com.example.spend.di.module.retrofit

import com.example.spend.data.api.currency.CurrencyApiService
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
    @Singleton
    fun provideRetrofit(
        json: Json
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.frankfurter.dev/")
        .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): CurrencyApiService = retrofit.create(
        CurrencyApiService::class.java
    )
}