package com.example.baro.feature.auth.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionManager(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
    }

    val accessToken: Flow<String?> = dataStore.data
        .map { prefs -> prefs[KEY_ACCESS_TOKEN] }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = token
        }
    }

    // 1) access token만 지우는 함수 (Splash에서 필요)
    suspend fun clearAccessToken() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
        }
    }
}
