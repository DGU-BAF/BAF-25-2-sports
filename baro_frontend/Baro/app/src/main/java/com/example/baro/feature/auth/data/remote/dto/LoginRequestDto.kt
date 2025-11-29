package com.example.baro.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable // import 추가

@Serializable // ✅ 어노테이션 추가
data class LoginRequestDto(
    val kakaoAccessToken: String
)
