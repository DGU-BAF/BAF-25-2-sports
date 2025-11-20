package com.example.baro.feature.auth.domain.model

/**
 * 최초 회원가입 / 추가 정보 입력 시 사용하는 요청 모델
 * - 사용자가 화면에서 직접 입력하는 필드만 포함
 * - uuid, kakaoId, sportsmanship, 위치 정보 등은 포함하지 않음
 */
data class SignUpRequest(
    val nickname: String,
    val birthDate: String,
    val gender: String,
    val height: Float,
    val weight: Float,
    val muscleMass: Float?,
    val skillLevel: String,
    val favoriteSports: List<String>
)
