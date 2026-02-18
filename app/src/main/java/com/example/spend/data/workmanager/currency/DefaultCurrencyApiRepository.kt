package com.example.spend.data.workmanager.currency

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import jakarta.inject.Inject
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class DefaultCurrencyApiRepository @Inject constructor(
    private val workManager: WorkManager
) : CurrencyApiRepository {
    override fun scheduleExchangeRateFetch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<CurrencyWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS,
        )
            .setInitialDelay(duration = getInitialDelay(), timeUnit = TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = "currency_exchange_rate_fetch",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
            request = work
        )
    }

    private fun getInitialDelay(): Long {
        val cetZone = ZoneId.of("Europe/Paris")
        val now = ZonedDateTime.now(cetZone)

        val target = now
            .withHour(16)
            .withMinute(0)
            .withSecond(0)

        if (target > now) {
            target.plusDays(1)
        }

        return Duration.between(now, target).toMillis()
    }
}