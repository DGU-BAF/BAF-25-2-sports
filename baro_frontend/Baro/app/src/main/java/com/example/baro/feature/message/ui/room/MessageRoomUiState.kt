package com.example.baro.feature.message.ui.room

import com.example.baro.feature.message.domain.model.Message

data class MessageRoomUiState(
    val isLoading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val errorMessage: String? = null
)
