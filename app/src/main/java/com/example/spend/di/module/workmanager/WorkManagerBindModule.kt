package com.example.spend.di.module.workmanager

import com.example.spend.data.workmanager.budget.BudgetNotificationRepository
import com.example.spend.data.workmanager.budget.DefaultBudgetNotificationRepository
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