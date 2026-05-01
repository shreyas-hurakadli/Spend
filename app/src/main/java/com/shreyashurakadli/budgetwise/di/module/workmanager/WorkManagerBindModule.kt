package com.shreyashurakadli.budgetwise.di.module.workmanager

import com.shreyashurakadli.budgetwise.data.workmanager.budget.BudgetNotificationRepository
import com.shreyashurakadli.budgetwise.data.workmanager.budget.DefaultBudgetNotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkManagerBindModule {
    @Binds
    abstract fun bindBudgetNotificationRepository(defaultBudgetNotificationRepository: DefaultBudgetNotificationRepository): BudgetNotificationRepository
}