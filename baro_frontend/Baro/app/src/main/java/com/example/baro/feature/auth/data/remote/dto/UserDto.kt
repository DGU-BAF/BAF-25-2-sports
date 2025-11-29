package com.example.baro.feature.auth.data.remote.dto

data class UserDto(
    val id: String,
    val kakaoId: String,
    val nickname: String?,              // 서버에서 null 줄 수 있음
    val birthDate: String?,             // null 가능
    val gender: String?,                // null 가능
    val height: Float?,                 // null 가능
    val weight: Float?,                 // null 가능
    val muscleMass: Float?,             // null 가능
    val skillLevel: String?,            // null 가능
    val favoriteSports: List<String>?,  // null 가능
    val sportsmanship: Float?,          // null 가능
    val latitude: Double?,              // 위치는 어차피 nullable 이 자연스러움
    val longitude: Double?
)