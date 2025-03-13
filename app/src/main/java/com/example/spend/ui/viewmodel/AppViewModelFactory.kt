package com.example.spend.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.spend.DefaultAppContainer

object AppViewModelFactory {
    val Factory = viewModelFactory {
        initializer {
            AddViewModel(
                defaultRepository = defaultRepositoryContainer().container.databaseRepository,
                dataStoreRepository = defaultRepositoryContainer().dataStoreRepository
            )
        }
        initializer {
            EntryViewModel(defaultRepository = defaultRepositoryContainer().container.databaseRepository)
        }
        initializer {
            HomeViewModel(
                defaultRepository = defaultRepositoryContainer().container.databaseRepository,
                dataStoreRepository = defaultRepositoryContainer().dataStoreRepository
            )
        }
        initializer {
            ExpenseViewModel(
                defaultRepository = defaultRepositoryContainer().container.databaseRepository,
                dataStoreRepository = defaultRepositoryContainer().dataStoreRepository
            )
        }
    }
}

fun CreationExtras.defaultRepositoryContainer(): DefaultAppContainer =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DefaultAppContainer)