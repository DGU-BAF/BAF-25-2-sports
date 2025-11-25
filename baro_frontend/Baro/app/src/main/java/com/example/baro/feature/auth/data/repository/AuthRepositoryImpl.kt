package com.example.baro.feature.auth.data.repository

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
        val response = authApi.loginWithKakao(LoginRequestDto(token))

        return if (response.isNewUser) {
            // 신규 회원 → 토큰 저장 안 함
            LoginResult(
                isNewUser = true,
                user = null
            )
        } else {
            // 기존 회원 → 토큰 저장 + 유저 정보 매핑
            // authLocalDataSource 인스턴스를 사용해야 함
            authLocalDataSource.saveAccessToken(response.accessToken!!)

            val user: AuthUser = AuthMapper.toDomain(response.user!!)

            LoginResult(
                isNewUser = false,
                user = user
            )
        }
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
