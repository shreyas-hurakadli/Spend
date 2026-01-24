package com.example.spend

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.spend.data.room.budget.BudgetRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.data.workmanager.budget.BudgetCheckWorker
import jakarta.inject.Inject

class NotificationWorkerFactory @Inject constructor(
    private val entryRepository: EntryRepository,
    private val budgetRepository: BudgetRepository
): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when(workerClassName) {
            BudgetCheckWorker::class.java.name -> {
                BudgetCheckWorker(
                    context = appContext,
                    params = workerParameters,
                    entryRepository = entryRepository,
                    budgetRepository = budgetRepository
                )
            }
            else -> null
        }
    }
}