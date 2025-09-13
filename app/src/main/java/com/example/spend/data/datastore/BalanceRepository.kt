package com.example.spend.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import com.example.spend.di.annotations.BalanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class BalanceRepository @Inject constructor(
    @BalanceRepository private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val BALANCE_AMOUNT = doublePreferencesKey("balance")
        val CURRENT_ACCOUNT = longPreferencesKey("current_account")
    }

    suspend fun saveBalance(balance: Double) {
        dataStore.edit { preferences ->
            preferences[BALANCE_AMOUNT] = balance
        }
    }

    suspend fun registerAccount(id: Long) {
        dataStore.edit { preferences ->
            preferences[CURRENT_ACCOUNT] = id
        }
    }

    val currentAccountId: Flow<Long> = dataStore.data
        .catch {
            if (it is IOException)
                emit(emptyPreferences())
            throw it
        }
        .map { preferences ->
            preferences[CURRENT_ACCOUNT] ?: 0L
        }

    val balance: Flow<Double> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else
                throw it
        }
        .map { preferences ->
            preferences[BALANCE_AMOUNT] ?: 0.00
        }
}