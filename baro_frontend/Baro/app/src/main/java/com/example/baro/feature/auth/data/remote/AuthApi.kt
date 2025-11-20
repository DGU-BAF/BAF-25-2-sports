package com.example.baro.feature.auth.data.remote

import com.example.baro.feature.auth.data.remote.dto.LoginRequestDto
import com.example.baro.feature.auth.data.remote.dto.LoginResponseDto
import com.example.baro.feature.auth.data.remote.dto.ProfileUpdateRequestDto
import com.example.baro.feature.auth.data.remote.dto.SignUpRequestDto
import com.example.baro.feature.auth.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApi {

    // 카카오 액세스토큰 → JWT + 유저정보
    @POST("auth/kakao-login")
    suspend fun loginWithKakao(
        @Body body: LoginRequestDto
    ): LoginResponseDto

    // 최초 회원가입
    @POST("auth/sign-up")
    suspend fun signUp(
        @Body body: SignUpRequestDto
    ): UserDto

    // 내 프로필 조회
    @GET("users/me")
    suspend fun getMyProfile(): UserDto

    // 프로필 수정
    @PATCH("users/me/profile")
    suspend fun updateProfile(
        @Body body: ProfileUpdateRequestDto
    ): UserDto

    // 서버 로그아웃 API를 쓰고 싶을 때 (선택)
    @POST("auth/logout")
    suspend fun logout()

    // 계정 탈퇴
    @DELETE("users/me")
    suspend fun withdraw()
}
