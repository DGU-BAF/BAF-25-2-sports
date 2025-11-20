package com.example.baro.feature.auth.data.remote.dto

data class SignUpRequestDto(
    val nickname: String,
    val birthDate: String,
    val gender: String,
    val height: Float,
    val weight: Float,
    val muscleMass: Float?,
    val skillLevel: String,
    val favoriteSports: List<String>
)
