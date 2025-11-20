package com.example.baro.feature.bot.domain.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val sender: SenderType,   // USER or BOT
    val timestamp: Long = System.currentTimeMillis()
)

enum class SenderType {
    USER,
    BOT
}
