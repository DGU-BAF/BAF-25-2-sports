package com.example.baro.feature.auth.data.mapper

import com.example.baro.feature.auth.data.remote.dto.ProfileUpdateRequestDto
import com.example.baro.feature.auth.data.remote.dto.SignUpRequestDto
import com.example.baro.feature.auth.data.remote.dto.UserDto
import com.example.baro.feature.auth.domain.model.AuthUser
import com.example.baro.feature.auth.domain.model.ProfileUpdateRequest
import com.example.baro.feature.auth.domain.model.SignUpRequest

object AuthMapper {

    fun toDomain(userDto: UserDto): AuthUser =
        AuthUser(
            id = userDto.id,
            kakaoId = userDto.kakaoId,

            // String? -> String (기본값 "")
            nickname = userDto.nickname.orEmpty(),

            // nullable 그대로 들고 가도 됨
            birthDate = userDto.birthDate,
            gender = userDto.gender,

            // Float? -> Float (기본값 0f 로 맞춰야 type mismatch 안 남)
            height = userDto.height ?: 0f,
            weight = userDto.weight ?: 0f,
            muscleMass = userDto.muscleMass,       // 그대로 nullable

            // String? -> String? (그대로 두거나 orEmpty() 로 바꾸고 싶으면 도메인도 String으로)
            skillLevel = userDto.skillLevel,

            // List<String>? -> List<String>
            favoriteSports = userDto.favoriteSports ?: emptyList(),

            // Float? -> Float
            sportsmanship = userDto.sportsmanship ?: 0f,

            // Double? 는 그대로
            latitude = userDto.latitude,
            longitude = userDto.longitude
        )

    fun toSignUpDto(request: SignUpRequest): SignUpRequestDto =
        SignUpRequestDto(
            nickname = request.nickname,
            birthDate = request.birthDate,
            gender = request.gender,
            height = request.height,
            weight = request.weight,
            muscleMass = request.muscleMass,
            skillLevel = request.skillLevel,
            favoriteSports = request.favoriteSports
        )

    fun toProfileUpdateDto(request: ProfileUpdateRequest): ProfileUpdateRequestDto =
        ProfileUpdateRequestDto(
            nickname = request.nickname,
            height = request.height,
            weight = request.weight,
            muscleMass = request.muscleMass,
            skillLevel = request.skillLevel,
            favoriteSports = request.favoriteSports,
            latitude = request.latitude,
            longitude = request.longitude
        )
}
