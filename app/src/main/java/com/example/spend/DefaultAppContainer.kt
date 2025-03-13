package com.example.spend

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.spend.data.datastore.BalanceRepository
import com.example.spend.data.room.DatabaseRepositoryContainer
import com.example.spend.data.room.RepositoryContainer

private const val BALANCE = "balance"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = BALANCE
)

class DefaultAppContainer: Application() {
    lateinit var container: RepositoryContainer
        private set

    lateinit var dataStoreRepository: BalanceRepository
        private set

    override fun onCreate() {
        super.onCreate()
        container = DatabaseRepositoryContainer(this)
        dataStoreRepository = BalanceRepository(dataStore)
    }
}