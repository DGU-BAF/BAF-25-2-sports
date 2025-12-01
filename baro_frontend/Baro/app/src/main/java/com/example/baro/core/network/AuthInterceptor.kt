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
        val originalRequest = chain.request()

        // DataStore 에서 accessToken 한 번만 동기적으로 가져오기
        val accessToken = runBlocking {
            sessionManager.accessToken.first()
        }

        // 토큰이 없으면 헤더 추가 없이 그대로 진행
        if (accessToken.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        // 백엔드에서 사용하는 형식: Authorization: Bearer <JWT>
        val authedRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(authedRequest)
    }
}
