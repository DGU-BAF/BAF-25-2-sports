// feature/bot/data/remote/RealBotRemoteDataSource.kt

package com.example.baro.feature.bot.data.remote

import com.example.baro.feature.bot.domain.model.ChatMessage
import com.example.baro.feature.bot.domain.model.ChatRoom
import com.example.baro.feature.bot.domain.model.ChatRoomSummary

class RealBotRemoteDataSource(
    private val api: BotApiService
) : BotRemoteDataSource {

    override suspend fun fetchChatRooms(): List<ChatRoomSummary> {
        // GET /bot/rooms
        return api.getChatRooms()
            .map { it.toDomain() }
    }

    override suspend fun fetchChatRoom(roomId: String): ChatRoom? {
        // GET /bot/rooms/{roomId}/messages
        val messages = api.getMessages(roomId)
        // 방이 아직 비어있을 수도 있으니 빈 방이라도 만들어서 반환
        return messages.toChatRoom(roomId = roomId, defaultTitle = "새 대화")
    }

    override suspend fun sendUserMessage(roomId: String, userMessage: String): List<ChatMessage> {
        // 1) POST /bot/rooms/{roomId}/messages (user → bot)
        api.sendMessage(
            roomId = roomId,
            request = BotRequestDto(text = userMessage)
        )
        // 2) 최신 대화 내역 전체를 다시 가져와서 반환
        return api.getMessages(roomId)
            .map { it.toDomain() }
    }
}
