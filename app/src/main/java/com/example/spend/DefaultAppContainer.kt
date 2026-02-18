package com.example.spend

import android.app.Application
import androidx.work.Configuration
import com.example.spend.data.workmanager.currency.DefaultCurrencyApiRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DefaultAppContainer : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: NotificationWorkerFactory

    @Inject
    lateinit var defaultCurrencyApiRepository: DefaultCurrencyApiRepository

    override fun onCreate() {
        super.onCreate()
        defaultCurrencyApiRepository.scheduleExchangeRateFetch()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}