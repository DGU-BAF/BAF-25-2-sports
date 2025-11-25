package com.example.baro.feature.auth.data.remote.dto

data class LoginResponseDto(
    val isNewUser: Boolean,
    val accessToken: String?,
    val user: UserDto?
)

