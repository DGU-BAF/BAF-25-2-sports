package com.example.baro.feature.auth.data.local

import kotlinx.coroutines.flow.Flow

class AuthLocalDataSource(
    private val sessionManager: SessionManager
) {

    val accessToken: Flow<String?> = sessionManager.accessToken

    suspend fun saveAccessToken(token: String) {
        sessionManager.saveAccessToken(token)
    }

    suspend fun clearSession() {
        sessionManager.clear()
    }
}
