@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.example.baro.core.network

import com.example.baro.feature.auth.data.local.SessionManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    // 실기기에서 확인한 FastAPI 주소
    const val BASE_URL: String = "http://192.168.45.157:8000/"

    // --- JSON 설정 (kotlinx) ---

    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    // --- OkHttp 기본 클라이언트 ---

    private val defaultOkHttpClient: OkHttpClient by lazy {
        createOkHttp()
    }

    // --- 기본 Retrofit (kotlinx-serialization 용) ---

    val retrofit: Retrofit by lazy {
        createRetrofit(
            baseUrl = BASE_URL,
            okHttpClient = defaultOkHttpClient,
            useKotlinx = true
        )
    }

    // --- Gson 전용 Retrofit (bot DTO 용) ---

    val gsonRetrofit: Retrofit by lazy {
        createRetrofit(
            baseUrl = BASE_URL,
            okHttpClient = defaultOkHttpClient,
            useKotlinx = false
        )
    }

    // --- OkHttp 생성 헬퍼 ---

    fun createOkHttp(
        extraInterceptors: List<Interceptor> = emptyList(),
        enableLogging: Boolean = true
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)

        if (enableLogging) {
            builder.addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
        }

        extraInterceptors.forEach { builder.addInterceptor(it) }

        return builder.build()
    }

    // --- Retrofit 생성 헬퍼 ---

    fun createRetrofit(
        baseUrl: String = BASE_URL,
        okHttpClient: OkHttpClient = defaultOkHttpClient,
        useKotlinx: Boolean = true
    ): Retrofit {
        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)

        return if (useKotlinx) {
            val contentType = "application/json".toMediaType()
            builder
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
        } else {
            builder
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    // --- 공통 API 생성 (kotlinx 기반) ---

    fun <T> createApi(service: Class<T>): T =
        retrofit.create(service)

    /**
     * JWT 자동 붙이는 Retrofit (auth 용)
     * 👉 여기서 이제 Gson 사용하도록 변경
     */
    fun createAuthorizedRetrofit(
        sessionManager: SessionManager
    ): Retrofit {
        val clientWithAuth = createOkHttp(
            extraInterceptors = listOf(AuthInterceptor(sessionManager))
        )
        return createRetrofit(
            baseUrl = BASE_URL,
            okHttpClient = clientWithAuth,
            useKotlinx = false   // 🔴 여기 true → false 로 변경
        )
    }

    /**
     * Gson 기반 API 생성 (bot DTO 용)
     */
    fun <T> createGsonApi(service: Class<T>): T =
        gsonRetrofit.create(service)
}
