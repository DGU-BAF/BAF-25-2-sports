package com.example.baro.feature.auth.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.auth.domain.model.AuthUser
import com.example.baro.feature.auth.domain.model.ProfileUpdateRequest
import com.example.baro.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileEditViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<AuthUser?>(null)
    val user: StateFlow<AuthUser?> = _user

    val nickname = MutableStateFlow("")
    val height = MutableStateFlow("")
    val weight = MutableStateFlow("")
    val muscleMass = MutableStateFlow("")
    val skillLevel = MutableStateFlow("")
    val favoriteSports = MutableStateFlow<List<String>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _updateSuccessEvent = MutableStateFlow(false)
    val updateSuccessEvent: StateFlow<Boolean> = _updateSuccessEvent

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching {
                authRepository.getMyProfile()
            }.onSuccess { authUser ->
                _user.value = authUser
                nickname.value = authUser.nickname
                height.value = authUser.height.toInt().toString()
                weight.value = authUser.weight.toInt().toString()
                muscleMass.value = authUser.muscleMass?.toInt()?.toString().orEmpty()
                skillLevel.value = authUser.skillLevel.orEmpty()
                favoriteSports.value = authUser.favoriteSports
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "프로필을 불러오지 못했습니다."
            }
            _isLoading.value = false
        }
    }

    fun setFavoriteSports(list: List<String>) {
        favoriteSports.value = list
    }

    fun updateProfile() {
        val current = _user.value ?: return

        // 변경된 것만 request에 넣기
        val nicknameNew = nickname.value.trim()
        val heightNew = height.value.toFloatOrNull()
        val weightNew = weight.value.toFloatOrNull()
        val muscleNew = muscleMass.value.toFloatOrNull()
        val skillNew = skillLevel.value.trim()
        val sportsNew = favoriteSports.value

        val request = ProfileUpdateRequest(
            nickname = if (nicknameNew.isNotEmpty() && nicknameNew != current.nickname) nicknameNew else null,
            height = if (heightNew != null && heightNew != current.height) heightNew else null,
            weight = if (weightNew != null && weightNew != current.weight) weightNew else null,
            muscleMass = if (muscleNew != null && muscleNew != current.muscleMass) muscleNew else null,
            skillLevel = if (skillNew.isNotEmpty() && skillNew != current.skillLevel) skillNew else null,
            favoriteSports = if (sportsNew.isNotEmpty() && sportsNew != current.favoriteSports) sportsNew else null,
            latitude = null,
            longitude = null
        )

        // 바뀐 게 하나도 없으면 그냥 성공 처리만
        if (request == ProfileUpdateRequest()) {
            _updateSuccessEvent.value = true
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            runCatching {
                authRepository.updateProfile(request)
            }.onSuccess { updated ->
                _user.value = updated
                _updateSuccessEvent.value = true
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "프로필 수정 중 오류가 발생했습니다."
            }

            _isLoading.value = false
        }
    }

    fun consumeUpdateSuccessEvent() {
        _updateSuccessEvent.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
