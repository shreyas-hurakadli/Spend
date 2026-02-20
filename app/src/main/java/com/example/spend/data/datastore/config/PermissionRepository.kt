package com.example.spend.data.datastore.config

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.spend.di.annotations.PermissionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class PermissionRepository @Inject constructor(
    @PermissionRepository private val dataStore: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private companion object {
        val POST_NOTIFICATIONS = booleanPreferencesKey("post_notifications")
    }

    suspend fun registerPostNotificationsPermission() {
        withContext(context = dispatcher) {
            dataStore.edit { preferences ->
                preferences[POST_NOTIFICATIONS] = true
            }
        }
    }

    val postNotifications = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else
                throw it
        }
        .map { preferences ->
            preferences[POST_NOTIFICATIONS] ?: false
        }
}