// com/example/baro/feature/auth/domain/repository/LoginResult.kt
package com.example.baro.feature.auth.domain.repository

import com.example.baro.feature.auth.domain.model.AuthUser

data class LoginResult(
    val isNewUser: Boolean,
    val user: AuthUser?
)
