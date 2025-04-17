package com.example.app.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val TOKEN = stringPreferencesKey("token")
        const val TAG = "UserPreferencesRepo"
    }
    val token: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            }
            else {
                throw it
            }
        }
        .map { preferences ->
        preferences[TOKEN] ?: ""
    }

    suspend fun saveTokenPreference (token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    suspend fun clearTokenPreference(){
        dataStore.edit { preferences ->
            preferences.remove(TOKEN)
        }
    }

}