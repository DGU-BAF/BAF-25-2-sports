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
            nickname = userDto.nickname,
            birthDate = userDto.birthDate,
            gender = userDto.gender,
            height = userDto.height,
            weight = userDto.weight,
            muscleMass = userDto.muscleMass,
            skillLevel = userDto.skillLevel,
            favoriteSports = userDto.favoriteSports,
            sportsmanship = userDto.sportsmanship,
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
