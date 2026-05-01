package com.shreyashurakadli.budgetwise.di.module.retrofit

import com.shreyashurakadli.budgetwise.data.api.currency.CurrencyRepository
import com.shreyashurakadli.budgetwise.data.api.currency.DefaultCurrencyRepository
import com.shreyashurakadli.budgetwise.data.workmanager.currency.CurrencyApiRepository
import com.shreyashurakadli.budgetwise.data.workmanager.currency.DefaultCurrencyApiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindModule {
    @Binds
    abstract fun bindCurrencyRepository(defaultCurrencyRepository: DefaultCurrencyRepository): CurrencyRepository

    @Binds
    abstract fun bindCurrencyApiRepository(defaultCurrencyApiRepository: DefaultCurrencyApiRepository): CurrencyApiRepository
}