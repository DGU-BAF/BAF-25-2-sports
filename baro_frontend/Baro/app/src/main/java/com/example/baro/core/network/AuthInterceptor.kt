package com.example.baro.core.network

import com.example.baro.feature.auth.data.local.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // DataStore(Flow) → 현재 토큰 1번만 동기적으로 꺼냄
        val token = runBlocking {
            sessionManager.accessToken.first()
        }

        val newRequest = if (token.isNullOrBlank()) {
            original
        } else {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(newRequest)
    }
}