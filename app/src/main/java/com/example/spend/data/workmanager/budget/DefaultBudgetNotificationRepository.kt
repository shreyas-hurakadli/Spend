package com.example.spend.data.workmanager.budget

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import jakarta.inject.Inject

class DefaultBudgetNotificationRepository @Inject constructor(
    private val workManager: WorkManager
) : BudgetNotificationRepository {
    override fun checkBudgetStatus() {
        val work = OneTimeWorkRequest
            .Builder(workerClass = BudgetCheckWorker::class.java)
            .build()
        workManager.enqueue(request = work)
    }
}