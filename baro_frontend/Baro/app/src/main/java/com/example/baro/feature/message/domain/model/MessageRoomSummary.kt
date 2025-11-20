// com/example/baro/feature/message/domain/model/MessageRoomSummary.kt
package com.example.baro.feature.message.domain.model

import java.time.LocalDateTime

data class MessageRoomSummary(
    val roomId: String,
    val roomName: String,
    val lastMessage: String,
    val lastMessageTime: LocalDateTime
)
