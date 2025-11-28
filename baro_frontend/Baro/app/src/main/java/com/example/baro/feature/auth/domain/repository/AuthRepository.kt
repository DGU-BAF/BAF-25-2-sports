package com.example.baro.feature.auth.domain.repository

import com.example.baro.feature.auth.domain.model.AuthUser
import com.example.baro.feature.auth.domain.model.ProfileUpdateRequest
import com.example.baro.feature.auth.domain.model.SignUpRequest

//Auth 관련 도메인 인터페이스
//- 구현체는 data 계층(AuthRepositoryImpl)에서 작성

interface AuthRepository {


//     카카오 액세스 토큰을 이용한 로그인
//     - 백엔드에 토큰 전달 → JWT + 유저 정보(AuthUser) 반환
    suspend fun loginWithKakao(kakaoAccessToken: String): LoginResult

//    최초 회원가입 / 추가 정보 저장
    suspend fun signUp(request: SignUpRequest): AuthUser

//    내 프로필 조회 (서버에서 sportsmanship 포함 최신 정보 가져오기)
    suspend fun getMyProfile(): AuthUser

//     프로필 수정 (일부 필드만 수정 가능)
//     - 서버는 JWT로 유저를 식별하고, request 의 null 이 아닌 필드만 수정
    suspend fun updateProfile(request: ProfileUpdateRequest): AuthUser

//    위치만 업데이트하는 편의 함수
    suspend fun updateLocation(latitude: Double, longitude: Double) {
        updateProfile(
            ProfileUpdateRequest(
                latitude = latitude,
                longitude = longitude
            )
        )
    }

//     로그아웃
//     - 로컬 토큰 삭제
//     - 필요하다면 백엔드에도 로그아웃 알림
    suspend fun logout()

//     계정 탈퇴
//     - 백엔드에서 유저 삭제
//     - 로컬 토큰/캐시 삭제
    suspend fun withdraw()
}
