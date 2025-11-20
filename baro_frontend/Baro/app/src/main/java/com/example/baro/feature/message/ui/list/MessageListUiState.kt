package com.example.baro.feature.message.ui.list

import com.example.baro.feature.message.domain.model.MessageRoomSummary

data class MessageListUiState(
    val isLoading: Boolean = false,
    val rooms: List<MessageRoomSummary> = emptyList(),
    val errorMessage: String? = null
)
