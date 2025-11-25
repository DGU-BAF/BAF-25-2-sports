package com.example.baro.feature.auth.ui.login

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

    // 기존 회원 → 메인으로 보내는 이벤트
    private val _loginSuccessEvent = MutableStateFlow(false)
    val loginSuccessEvent: StateFlow<Boolean> = _loginSuccessEvent

    // 신규 회원 → 회원가입 화면으로 보내는 이벤트
    private val _navigateToSignupEvent = MutableStateFlow(false)
    val navigateToSignupEvent: StateFlow<Boolean> = _navigateToSignupEvent


    fun loginWithKakaoToken(kakaoAccessToken: String) {
        if (kakaoAccessToken.isBlank()) {
            _errorMessage.value = "카카오 토큰이 비어 있습니다."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            runCatching {
                authRepository.loginWithKakao(kakaoAccessToken)
            }.onSuccess { result ->
                _user.value = result.user

                if (result.isNewUser) {
                    // 신규 회원 → 회원가입 화면으로
                    _navigateToSignupEvent.value = true
                } else {
                    // 기존 회원 → 메인으로
                    _loginSuccessEvent.value = true
                }
            }.onFailure { e ->
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
