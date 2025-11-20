package com.example.baro.feature.bot.domain.model

import java.util.UUID

data class ChatRoom(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    var lastMessage: String,                      // ← var 로 변경
    val messages: MutableList<ChatMessage> = mutableListOf(),
    val createdAt: Long = System.currentTimeMillis()
)
