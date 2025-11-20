package com.example.baro.feature.bot.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.bot.data.BotRepository
import com.example.baro.feature.bot.domain.usecase.LoadChatRoomsUseCase
import com.example.baro.feature.bot.domain.usecase.CreateChatRoomUseCase
import com.example.baro.feature.bot.ui.model.BotChatListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BotChatListViewModel(
    private val repository: BotRepository
) : ViewModel() {

    // 필요하다면 UseCase 로 래핑
    private val loadChatRoomsUseCase = LoadChatRoomsUseCase(repository)
    private val createChatRoomUseCase = CreateChatRoomUseCase(repository)

    private val _uiState = MutableStateFlow(BotChatListUiState())
    val uiState: StateFlow<BotChatListUiState> = _uiState

    init {
        loadRooms()
    }

    fun loadRooms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val rooms = loadChatRoomsUseCase()
                _uiState.update { it.copy(isLoading = false, rooms = rooms) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "대화 목록을 불러오지 못했습니다."
                    )
                }
            }
        }
    }

    fun createNewRoom(onCreated: (roomId: String) -> Unit) {
        viewModelScope.launch {
            try {
                val room = createChatRoomUseCase(initialMessage = null)
                // 목록 새로고침
                loadRooms()
                onCreated(room.id)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "새 대화를 만들지 못했습니다.")
                }
            }
        }
    }
}
