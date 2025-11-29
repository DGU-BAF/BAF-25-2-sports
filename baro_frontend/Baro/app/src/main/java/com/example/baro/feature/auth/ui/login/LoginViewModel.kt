package com.example.baro.feature.auth.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.auth.domain.model.AuthUser
import com.example.baro.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<AuthUser?>(null)
    val user: StateFlow<AuthUser?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loginSuccessEvent = MutableStateFlow(false)
    val loginSuccessEvent: StateFlow<Boolean> = _loginSuccessEvent

    private val _navigateToSignupEvent = MutableStateFlow(false)
    val navigateToSignupEvent: StateFlow<Boolean> = _navigateToSignupEvent


    fun loginWithKakaoToken(kakaoAccessToken: String) {
        if (kakaoAccessToken.isBlank()) {
            _errorMessage.value = "카카오 토큰이 비어 있습니다."
            return
        }

        Log.d("LoginVM", "loginWithKakaoToken() called, token=$kakaoAccessToken")

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            runCatching {
                Log.d("LoginVM", "calling authRepository.loginWithKakao()")
                authRepository.loginWithKakao(kakaoAccessToken)
            }.onSuccess { result ->
                Log.d(
                    "LoginVM",
                    "loginWithKakaoToken success, isNewUser=${result.isNewUser}, user=${result.user}"
                )

                _user.value = result.user

                if (result.isNewUser) {
                    _navigateToSignupEvent.value = true
                } else {
                    _loginSuccessEvent.value = true
                }
            }.onFailure { e ->
                Log.e("LoginVM", "loginWithKakaoToken failed", e)
                _errorMessage.value = e.message ?: "로그인 중 오류가 발생했습니다."
            }

            _isLoading.value = false
        }
    }

    fun consumeLoginSuccessEvent() {
        _loginSuccessEvent.value = false
    }

    fun consumeNavigateToSignupEvent() {
        _navigateToSignupEvent.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
