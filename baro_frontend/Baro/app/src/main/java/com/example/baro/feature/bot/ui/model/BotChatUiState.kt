package com.example.baro.feature.bot.ui.model

import com.example.baro.feature.bot.domain.model.ChatRoom
import com.example.baro.feature.bot.domain.model.ChatRoomSummary
import com.example.baro.feature.bot.domain.model.ChatMessage

data class BotChatListUiState(
    val isLoading: Boolean = false,
    val rooms: List<ChatRoomSummary> = emptyList(),
    val errorMessage: String? = null
)

data class BotChatRoomUiState(
    val isLoading: Boolean = false,
    val room: ChatRoom? = null,
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val errorMessage: String? = null
)
