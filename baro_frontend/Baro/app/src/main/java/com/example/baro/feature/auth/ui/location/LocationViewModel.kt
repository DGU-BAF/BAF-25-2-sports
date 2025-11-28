package com.example.baro.feature.auth.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.baro.GlobalApplication
import com.example.baro.core.network.NetworkModule
import com.example.baro.feature.auth.data.local.AuthLocalDataSource
import com.example.baro.feature.auth.data.local.SessionManager
import com.example.baro.feature.auth.data.remote.AuthApi
import com.example.baro.feature.auth.data.repository.AuthRepositoryImpl
import com.example.baro.feature.auth.domain.model.ProfileUpdateRequest
import com.example.baro.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class LocationUiState {
        object Idle : LocationUiState()
        object Loading : LocationUiState()
        object Success : LocationUiState()
        data class Error(val message: String?) : LocationUiState()
    }

    private val _locationState = MutableStateFlow<LocationUiState>(LocationUiState.Idle)
    val locationState: StateFlow<LocationUiState> = _locationState

    fun updateLocation(lat: Double, lng: Double) {
        viewModelScope.launch {
            _locationState.value = LocationUiState.Loading
            try {
                val request = ProfileUpdateRequest(
                    latitude = lat,
                    longitude = lng
                )
                authRepository.updateProfile(request)
                _locationState.value = LocationUiState.Success
            } catch (e: Exception) {
                _locationState.value = LocationUiState.Error(e.message)
            }
        }
    }

    companion object {
        fun Factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                val authApi = NetworkModule.createApi(AuthApi::class.java)

                val sessionManager = SessionManager(GlobalApplication.instance.dataStore)
                val localDataSource = AuthLocalDataSource(sessionManager)

                val authRepository: AuthRepository =
                    AuthRepositoryImpl(authApi, localDataSource)

                return LocationViewModel(authRepository) as T
            }
        }
    }
}
