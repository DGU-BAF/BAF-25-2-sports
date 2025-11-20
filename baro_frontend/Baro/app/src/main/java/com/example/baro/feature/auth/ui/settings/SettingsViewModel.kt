package com.example.baro.feature.auth.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.auth.domain.model.AuthUser
import com.example.baro.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<AuthUser?>(null)
    val user: StateFlow<AuthUser?> = _user

    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent: StateFlow<Boolean> = _logoutEvent

    private val _withdrawEvent = MutableStateFlow(false)
    val withdrawEvent: StateFlow<Boolean> = _withdrawEvent

    fun loadProfile() {
        viewModelScope.launch {
            runCatching { authRepository.getMyProfile() }
                .onSuccess { _user.value = it }
                .onFailure {
                    // 필요하면 에러 처리 추가
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            runCatching { authRepository.logout() }
                .onSuccess { _logoutEvent.value = true }
        }
    }

    fun withdraw() {
        viewModelScope.launch {
            runCatching { authRepository.withdraw() }
                .onSuccess { _withdrawEvent.value = true }
        }
    }
}
