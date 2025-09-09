package com.example.spend.data.datastore.config

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.spend.di.annotations.LoginRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class LoginRepository @Inject constructor(
    @LoginRepository private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val FIRST_LOGIN = booleanPreferencesKey("first_login")
    }

    suspend fun registerFirstLogin() {
        dataStore.edit { preferences ->
            preferences[FIRST_LOGIN] = false;
        }
    }

    val firstLogin = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else
                throw it
        }
        .map { preferences ->
            preferences[FIRST_LOGIN] ?: false
        }
}