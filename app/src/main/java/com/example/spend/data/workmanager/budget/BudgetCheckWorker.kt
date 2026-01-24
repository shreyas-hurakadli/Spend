package com.example.spend.data.workmanager.budget

import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.spend.data.notification.NotificationChannelId
import com.example.spend.data.notification.NotificationService
import com.example.spend.data.room.budget.BudgetRepository
import com.example.spend.data.room.entry.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private const val BUDGET_ALERTS = "Budget Alerts"
private const val BUDGET_EXCEEDED_TITLE = "Budget Exceeded"
private const val BUDGET_EXCEEDED_TEXT = "Oops! You've exceeded your budget"
private const val BUDGET_FULL_TITLE = "Budget Limit Reached"
private const val BUDGET_FULL_TEXT =
    "You've used your entire budget. Any new expenses will exceed your limit"

private const val BUDGET_EXCEEDED_NOTIFICATION_ID = 0
private const val BUDGET_FULL_NOTIFICATION_ID = 1

class BudgetCheckWorker(
    val context: Context,
    params: WorkerParameters,
    private val entryRepository: EntryRepository,
    private val budgetRepository: BudgetRepository
) : CoroutineWorker(appContext = context, params = params) {
    override suspend fun doWork(): Result {
        return try {
            val budgets = withContext(context = Dispatchers.IO) {
                budgetRepository.getAllActiveBudgets().first()
            }
            budgets.forEach { budget ->
                val expense = withContext(context = Dispatchers.IO) {
                    when {
                        budget.accountId == 1L && budget.categoryId == 2L -> {
                            entryRepository.getExpenseByBudgetConstraintsUsingOnlyTime(
                                startTime = budget.startTimeStamp,
                                endTime = budget.startTimeStamp + budget.period
                            )
                        }

                        budget.accountId == 1L -> {
                            entryRepository.getExpenseByBudgetConstraintsUsingCategory(
                                categoryId = budget.categoryId,
                                startTime = budget.startTimeStamp,
                                endTime = budget.startTimeStamp + budget.period
                            )
                        }

                        budget.categoryId == 2L -> {
                            entryRepository.getExpenseByBudgetConstraintsUsingAccount(
                                accountId = budget.accountId,
                                startTime = budget.startTimeStamp,
                                endTime = budget.startTimeStamp + budget.period
                            )
                        }

                        else -> {
                            entryRepository.getExpenseByBudgetConstraints(
                                accountId = budget.accountId,
                                categoryId = budget.categoryId,
                                startTime = budget.startTimeStamp,
                                endTime = budget.startTimeStamp + budget.period
                            )
                        }
                    }
                }.first()

                if (expense > budget.amount) {
                    val notificationService = NotificationService(
                        context = context,
                        channelId = NotificationChannelId.BUDGET_EXCEEDED.name,
                        name = BUDGET_ALERTS,
                        importance = NotificationManager.IMPORTANCE_HIGH
                    )
                    notificationService.createNotification(
                        notificationId = BUDGET_EXCEEDED_NOTIFICATION_ID,
                        contentTitle = BUDGET_EXCEEDED_TITLE,
                        contentText = BUDGET_EXCEEDED_TEXT,
                        priority = NotificationManager.IMPORTANCE_HIGH,
                    )
                } else if (expense == budget.amount) {
                    val notificationService = NotificationService(
                        context = context,
                        channelId = NotificationChannelId.BUDGET_FULL.name,
                        name = BUDGET_ALERTS,
                        importance = NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationService.createNotification(
                        notificationId = BUDGET_FULL_NOTIFICATION_ID,
                        contentTitle = BUDGET_FULL_TITLE,
                        contentText = BUDGET_FULL_TEXT,
                        priority = NotificationManager.IMPORTANCE_HIGH,
                    )
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}