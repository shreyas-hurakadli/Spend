package com.example.spend.di.module.retrofit

import com.example.spend.data.api.currency.CurrencyRepository
import com.example.spend.data.api.currency.DefaultCurrencyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindModule {
    @Binds
    abstract fun bindCurrencyRepository(defaultCurrencyRepository: DefaultCurrencyRepository): CurrencyRepository
}