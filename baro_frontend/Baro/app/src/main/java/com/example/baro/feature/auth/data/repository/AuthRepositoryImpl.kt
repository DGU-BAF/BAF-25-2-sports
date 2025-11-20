package com.example.baro.feature.auth.data.repository

import com.example.baro.feature.auth.data.local.AuthLocalDataSource
import com.example.baro.feature.auth.data.mapper.AuthMapper
import com.example.baro.feature.auth.data.remote.AuthApi
import com.example.baro.feature.auth.data.remote.dto.LoginRequestDto
import com.example.baro.feature.auth.domain.model.AuthUser
import com.example.baro.feature.auth.domain.model.ProfileUpdateRequest
import com.example.baro.feature.auth.domain.model.SignUpRequest
import com.example.baro.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val authLocalDataSource: AuthLocalDataSource
) : AuthRepository {

    override suspend fun loginWithKakao(kakaoAccessToken: String): AuthUser {
        val response = authApi.loginWithKakao(LoginRequestDto(kakaoAccessToken))
        // JWT 저장
        authLocalDataSource.saveAccessToken(response.accessToken)
        // 유저 정보 도메인으로 변환
        return AuthMapper.toDomain(response.user)
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
        // 서버에 로그아웃 API가 있으면 호출
        runCatching { authApi.logout() }
        // 로컬 세션 제거
        authLocalDataSource.clearSession()
    }

    override suspend fun withdraw() {
        // 서버 계정 삭제
        authApi.withdraw()
        // 로컬 세션 제거
        authLocalDataSource.clearSession()
    }
}
