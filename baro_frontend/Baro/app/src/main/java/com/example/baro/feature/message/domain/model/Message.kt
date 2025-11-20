// com/example/baro/feature/message/domain/model/Message.kt
package com.example.baro.feature.message.domain.model

import java.time.LocalDateTime

data class Message(
    val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val createdAt: LocalDateTime,
    val isMine: Boolean
)
