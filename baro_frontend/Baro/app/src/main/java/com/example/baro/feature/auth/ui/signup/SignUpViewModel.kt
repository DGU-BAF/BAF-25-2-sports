package com.example.baro.feature.auth.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.auth.domain.model.AuthUser
import com.example.baro.feature.auth.domain.model.SignUpRequest
import com.example.baro.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 폼 상태 (필요한 것만 적어둠)
    val nickname = MutableStateFlow("")
    val birthDate = MutableStateFlow("")      // "YYYY-MM-DD"
    val gender = MutableStateFlow("")        // "male"/"female" 등
    val height = MutableStateFlow("")
    val weight = MutableStateFlow("")
    val muscleMass = MutableStateFlow("")
    val skillLevel = MutableStateFlow("")    // beginner/intermediate/advanced
    val favoriteSports = MutableStateFlow<List<String>>(emptyList())

    private val _user = MutableStateFlow<AuthUser?>(null)
    val user: StateFlow<AuthUser?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _signUpSuccessEvent = MutableStateFlow(false)
    val signUpSuccessEvent: StateFlow<Boolean> = _signUpSuccessEvent

    fun setFavoriteSports(list: List<String>) {
        favoriteSports.value = list
    }

    fun signUp() {
        // 아주 간단한 최소 검증만 (상세 검증은 나중에 추가)
        if (nickname.value.isBlank()) {
            _errorMessage.value = "닉네임을 입력해주세요."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val request = SignUpRequest(
                nickname = nickname.value,
                birthDate = birthDate.value,
                gender = gender.value,
                height = height.value.toFloatOrNull() ?: 0f,
                weight = weight.value.toFloatOrNull() ?: 0f,
                muscleMass = muscleMass.value.toFloatOrNull(),
                skillLevel = skillLevel.value,
                favoriteSports = favoriteSports.value
            )

            runCatching {
                authRepository.signUp(request)
            }.onSuccess { authUser ->
                _user.value = authUser
                _signUpSuccessEvent.value = true
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "회원가입 중 오류가 발생했습니다."
            }

            _isLoading.value = false
        }
    }

    fun consumeSignUpSuccessEvent() {
        _signUpSuccessEvent.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
