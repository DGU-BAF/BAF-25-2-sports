package com.example.baro.feature.auth.data.remote.dto

data class LoginResponseDto(
    val accessToken: String,
    val user: UserDto
)
