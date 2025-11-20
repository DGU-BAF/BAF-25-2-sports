package com.example.baro.feature.bot.ui.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.bot.data.BotRepository
import com.example.baro.feature.bot.domain.usecase.GetChatRoomUseCase
import com.example.baro.feature.bot.domain.usecase.SendUserMessageUseCase
import com.example.baro.feature.bot.ui.model.BotChatRoomUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BotChatRoomViewModel(
    private val repository: BotRepository
) : ViewModel() {

    private val getChatRoomUseCase = GetChatRoomUseCase(repository)
    private val sendUserMessageUseCase = SendUserMessageUseCase(repository)

    private val _uiState = MutableStateFlow(BotChatRoomUiState())
    val uiState: StateFlow<BotChatRoomUiState> = _uiState

    private var currentRoomId: String? = null

    fun loadRoom(roomId: String) {
        if (currentRoomId == roomId && _uiState.value.room != null) return

        currentRoomId = roomId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val room = getChatRoomUseCase(roomId)
                if (room == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "대화방을 찾을 수 없습니다."
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        room = room,
                        messages = room.messages.toList()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "대화방을 불러오지 못했습니다."
                    )
                }
            }
        }
    }

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val roomId = currentRoomId ?: return
        val messageText = _uiState.value.inputText.trim()
        if (messageText.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val room = sendUserMessageUseCase(roomId, messageText)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        room = room,
                        messages = room.messages.toList(),
                        inputText = ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "메시지를 보내지 못했습니다."
                    )
                }
            }
        }
    }
}
