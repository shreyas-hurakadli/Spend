package com.shreyashurakadli.budgetwise

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.shreyashurakadli.budgetwise.data.datastore.config.PreferencesRepository
import com.shreyashurakadli.budgetwise.data.room.budget.BudgetRepository
import com.shreyashurakadli.budgetwise.data.room.currency.CurrencyRepository
import com.shreyashurakadli.budgetwise.data.room.entry.EntryRepository
import com.shreyashurakadli.budgetwise.data.workmanager.budget.BudgetCheckWorker
import com.shreyashurakadli.budgetwise.data.workmanager.currency.CurrencyWorker
import jakarta.inject.Inject

class WorkManagerFactory @Inject constructor(
    private val entryRepository: EntryRepository,
    private val budgetRepository: BudgetRepository,
    private val currencyRepository: CurrencyRepository,
    private val apiCurrencyRepository: com.shreyashurakadli.budgetwise.data.api.currency.CurrencyRepository,
    private val defaultPreferencesRepository: PreferencesRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            BudgetCheckWorker::class.java.name -> {
                BudgetCheckWorker(
                    context = appContext,
                    params = workerParameters,
                    entryRepository = entryRepository,
                    budgetRepository = budgetRepository
                )
            }

            CurrencyWorker::class.java.name -> {
                CurrencyWorker(
                    context = appContext,
                    params = workerParameters,
                    defaultCurrencyRepository = apiCurrencyRepository,
                    dbCurrencyRepository = currencyRepository,
                    defaultPreferencesRepository = defaultPreferencesRepository
                )
            }

            else -> null
        }
    }
}