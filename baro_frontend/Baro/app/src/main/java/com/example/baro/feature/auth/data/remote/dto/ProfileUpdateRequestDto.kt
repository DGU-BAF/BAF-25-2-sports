package com.example.baro.feature.auth.data.remote.dto

data class ProfileUpdateRequestDto(
    val nickname: String? = null,
    val height: Float? = null,
    val weight: Float? = null,
    val muscleMass: Float? = null,
    val skillLevel: String? = null,
    val favoriteSports: List<String>? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
