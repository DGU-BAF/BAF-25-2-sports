// feature/bot/data/remote/Mappers.kt

package com.example.baro.feature.bot.data.remote

import com.example.baro.feature.bot.domain.model.ChatMessage
import com.example.baro.feature.bot.domain.model.ChatRoom
import com.example.baro.feature.bot.domain.model.ChatRoomSummary
import com.example.baro.feature.bot.domain.model.SenderType

// /bot/rooms → List<ChatRoomSummaryDto>
fun ChatRoomSummaryDto.toDomain(): ChatRoomSummary =
    ChatRoomSummary(
        id = id,
        title = title,
        lastMessage = lastMessage,
        createdAt = createdAt
    )

// /bot/rooms/{roomId}/messages → List<ChatMessageDto>
fun ChatMessageDto.toDomain(): ChatMessage =
    ChatMessage(
        id = id,
        text = text,
        sender = if (sender == "USER") SenderType.USER else SenderType.BOT,
        timestamp = timestamp
    )

// 메시지 리스트를 이용해 ChatRoom 조립
fun List<ChatMessageDto>.toChatRoom(roomId: String, defaultTitle: String = "새 대화"): ChatRoom {
    val messages = this.map { it.toDomain() }
    val lastText = messages.lastOrNull()?.text.orEmpty()
    return ChatRoom(
        id = roomId,
        title = defaultTitle,
        lastMessage = lastText,
        messages = messages.toMutableList()
    )
}
