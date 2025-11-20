package com.example.baro.feature.auth.domain.model

/**
 * 앱 내부에서 사용하는 로그인 유저 정보
 * (프로필 화면, 설정, 파티 참여자 정보 등에서 공통 사용)
 */
data class AuthUser(
    val id: String,                 // DB uuid
    val kakaoId: String,            // 카카오 계정 식별자 (읽기 전용)

    val nickname: String,
    val birthDate: String,          // "YYYY-MM-DD"
    val gender: String,             // "male" / "female" / 기타 정의

    val height: Float,              // cm
    val weight: Float,              // kg
    val muscleMass: Float?,         // 골격근량 (선택)

    val skillLevel: String,         // "beginner" / "intermediate" / "advanced" 등
    val favoriteSports: List<String>,

    val sportsmanship: Float,       // 매너온도 (서버 계산값, 클라이언트 수정 X)

    val latitude: Double?,          // 마지막으로 저장된 위치 (선택)
    val longitude: Double?          // 마지막으로 저장된 위치 (선택)
)
