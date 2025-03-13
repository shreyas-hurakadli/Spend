package com.example.spend.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class BalanceRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val BALANCE_AMOUNT = intPreferencesKey("balance")
        const val TAG = "BalanceRepository"
    }

    suspend fun saveBalance(balance: Int) {
        dataStore.edit { preferences ->
            preferences[BALANCE_AMOUNT] = balance
        }
    }

    val balance: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preference", it)
                emit(emptyPreferences())
            } else
                throw it
        }
        .map {  preferences ->
            preferences[BALANCE_AMOUNT] ?: 0
        }
}