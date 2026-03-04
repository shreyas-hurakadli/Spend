package com.example.spend.di.module.local

import com.example.spend.data.local.file.CsvExportableRepository
import com.example.spend.data.local.file.DefaultCsvExportableRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalBindModule {
    @Binds
    abstract fun bindDefaultCsvExportableRepository(defaultCsvExportableRepository: DefaultCsvExportableRepository): CsvExportableRepository
}