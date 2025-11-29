package com.example.baro.feature.auth.data.repository

import android.util.Log
import com.example.baro.feature.auth.data.local.AuthLocalDataSource
import com.example.baro.feature.auth.data.mapper.AuthMapper
import com.example.baro.feature.auth.data.remote.AuthApi
import com.example.baro.feature.auth.data.remote.dto.LoginRequestDto
import com.example.baro.feature.auth.domain.model.AuthUser
import com.example.baro.feature.auth.domain.model.ProfileUpdateRequest
import com.example.baro.feature.auth.domain.model.SignUpRequest
import com.example.baro.feature.auth.domain.repository.AuthRepository
import com.example.baro.feature.auth.domain.repository.LoginResult

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val authLocalDataSource: AuthLocalDataSource
) : AuthRepository {

    override suspend fun loginWithKakao(token: String): LoginResult {
        // 프론트 → 서버로 보내는 바디
        val request = LoginRequestDto(
            kakaoAccessToken = token
        )

        Log.d("AuthRepository", "loginWithKakao() request = $request")

        // 서버 호출
        val response = authApi.loginWithKakao(request)

        Log.d(
            "AuthRepository",
            "loginWithKakao() response: isNewUser=${response.isNewUser}, " +
                    "accessToken=${response.accessToken}, user=${response.user}"
        )

        // accessToken 저장
        val backendToken = response.accessToken
        if (!backendToken.isNullOrBlank()) {
            Log.d("AuthRepository", "Saving backend accessToken to DataStore")
            authLocalDataSource.saveAccessToken(backendToken)
        } else {
            Log.w(
                "AuthRepository",
                "backend accessToken is null or blank (isNewUser=${response.isNewUser})"
            )
        }

        // 유저 매핑
        val user: AuthUser? = response.user?.let { dto ->
            AuthMapper.toDomain(dto)
        }

        return LoginResult(
            isNewUser = response.isNewUser,
            user = user
        )
    }

    override suspend fun signUp(request: SignUpRequest): AuthUser {
        val dto = AuthMapper.toSignUpDto(request)
        val userDto = authApi.signUp(dto)
        return AuthMapper.toDomain(userDto)
    }

    override suspend fun getMyProfile(): AuthUser {
        val userDto = authApi.getMyProfile()
        return AuthMapper.toDomain(userDto)
    }

    override suspend fun updateProfile(request: ProfileUpdateRequest): AuthUser {
        val dto = AuthMapper.toProfileUpdateDto(request)
        val userDto = authApi.updateProfile(dto)
        return AuthMapper.toDomain(userDto)
    }

    override suspend fun logout() {
        runCatching { authApi.logout() }
        authLocalDataSource.clearSession()
    }

    override suspend fun withdraw() {
        authApi.withdraw()
        authLocalDataSource.clearSession()
    }
}
