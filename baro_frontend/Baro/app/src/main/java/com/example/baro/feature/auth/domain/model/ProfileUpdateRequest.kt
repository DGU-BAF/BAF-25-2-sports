package com.example.baro.feature.auth.domain.model

/**
 * 로그인 후 프로필 일부 수정에 사용하는 요청 모델
 * - 수정 가능 필드만 포함
 * - null 인 필드는 "이번 요청에서 수정하지 않는다"는 의미
 * - birthDate, gender 는 수정 불가라고 가정해서 제외
 */
data class ProfileUpdateRequest(
    val nickname: String? = null,
    val height: Float? = null,
    val weight: Float? = null,
    val muscleMass: Float? = null,
    val skillLevel: String? = null,
    val favoriteSports: List<String>? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
