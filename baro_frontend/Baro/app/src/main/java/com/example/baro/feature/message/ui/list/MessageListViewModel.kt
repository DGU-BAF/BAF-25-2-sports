package com.example.baro.feature.message.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.message.domain.usecase.GetMessageRoomsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessageListViewModel(
    private val getMessageRoomsUseCase: GetMessageRoomsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageListUiState())
    val uiState: StateFlow<MessageListUiState> = _uiState.asStateFlow()

    init {
        loadMessageRooms()
    }

    fun loadMessageRooms() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            runCatching {
                getMessageRoomsUseCase()
            }.onSuccess { rooms ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    rooms = rooms,
                    errorMessage = null
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "메시지 목록을 불러오지 못했습니다."
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            getMessageRoomsUseCase: GetMessageRoomsUseCase
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MessageListViewModel::class.java)) {
                    return MessageListViewModel(getMessageRoomsUseCase) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
            }
        }
    }
}
