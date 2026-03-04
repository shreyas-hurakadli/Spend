package com.example.spend.data.datastore.config

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.spend.di.annotations.PreferencesRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    @PreferencesRepository private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val BASE_CURRENCY = stringPreferencesKey(name = "base_currency")
        val BASE_CURRENCY_SYMBOL = stringPreferencesKey(name = "base_currency_symbol")
        val TIME_FORMAT = stringPreferencesKey(name = "time_format")
    }

    suspend fun registerBaseCurrency(baseCurrency: String) {
        dataStore.edit { preferences ->
            preferences[BASE_CURRENCY] = baseCurrency.substring(startIndex = 0, endIndex = 3)
            preferences[BASE_CURRENCY_SYMBOL] = baseCurrency.substring(startIndex = 4)
        }
    }

    suspend fun changeTimeFormat(format: String) {
        dataStore.edit { preferences ->
            preferences[TIME_FORMAT] = format
        }
    }

    val baseCurrency = dataStore.data
        .catch { emit(value = emptyPreferences()) }
        .map { preferences -> preferences[BASE_CURRENCY] ?: "" }

    val baseCurrencySymbol = dataStore.data
        .catch { emit(value = emptyPreferences()) }
        .map { preferences -> preferences[BASE_CURRENCY_SYMBOL] ?: "" }

    val timeFormat = dataStore.data
        .catch { emit(value = emptyPreferences()) }
        .map { preferences -> preferences[TIME_FORMAT] ?: "12h" }
}