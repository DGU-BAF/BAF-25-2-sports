package com.example.baro.feature.bot.domain.model

data class ChatRoomSummary(
    val id: String,
    val title: String,
    val lastMessage: String,
    val createdAt: Long
)
