package com.example.baro.feature.message.ui.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.message.domain.model.Message
import com.example.baro.feature.message.domain.usecase.GetMessagesUseCase
import com.example.baro.feature.message.domain.usecase.ObserveMessagesUseCase
import com.example.baro.feature.message.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MessageRoomViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageRoomUiState())
    val uiState: StateFlow<MessageRoomUiState> = _uiState.asStateFlow()

    private var currentRoomId: String? = null
    private var isInitialized: Boolean = false

    fun init(roomId: String) {
        if (isInitialized) return
        isInitialized = true
        currentRoomId = roomId

        loadInitialMessages()
        observeIncomingMessages()
    }

    private fun loadInitialMessages() {
        val roomId = currentRoomId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                getMessagesUseCase(roomId)
            }.onSuccess { messages ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        messages = messages,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "메시지를 불러오지 못했습니다."
                    )
                }
            }
        }
    }

    private fun observeIncomingMessages() {
        val roomId = currentRoomId ?: return
        viewModelScope.launch {
            observeMessagesUseCase(roomId).collect { message: Message ->
                _uiState.update { state ->
                    state.copy(
                        messages = state.messages + message
                    )
                }
            }
        }
    }

    fun sendMessage(content: String) {
        val roomId = currentRoomId ?: return
        if (content.isBlank()) return

        viewModelScope.launch {
            runCatching {
                sendMessageUseCase(roomId, content)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        errorMessage = throwable.message ?: "메시지를 전송하지 못했습니다."
                    )
                }
            }
        }
    }
}
