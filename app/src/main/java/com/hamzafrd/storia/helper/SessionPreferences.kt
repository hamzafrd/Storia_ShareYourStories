package com.hamzafrd.storia.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val tokenKey = stringPreferencesKey(TOKEN_KEY)
    private val sessionKey = booleanPreferencesKey(SESSION_KEY)
    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[tokenKey] ?: ""
        }
    }

    suspend fun deleteSession() {
        dataStore.edit {
            it.clear()
        }
    }

    suspend fun saveSessionSetting(isSessionActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[sessionKey] = isSessionActive
        }
    }

    suspend fun saveTokenSetting(token: String) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    companion object {
        private const val SESSION_KEY = "session_key"
        private const val TOKEN_KEY = "token_key"

        @Volatile
        private var INSTANCE: SessionPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SessionPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
