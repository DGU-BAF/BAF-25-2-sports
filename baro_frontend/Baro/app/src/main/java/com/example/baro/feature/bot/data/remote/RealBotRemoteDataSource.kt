package com.example.baro.feature.bot.data.remote

import com.example.baro.feature.bot.domain.model.ChatMessage
import com.example.baro.feature.bot.domain.model.ChatRoom
import com.example.baro.feature.bot.domain.model.ChatRoomSummary
import com.example.baro.feature.bot.domain.model.SenderType

class RealBotRemoteDataSource(
    private val api: BotApiService
) : BotRemoteDataSource {

    // 1) 채팅방 목록
    override suspend fun fetchChatRooms(): List<ChatRoomSummary> {
        android.util.Log.d("BotRemote", "fetchChatRooms() called")
        val dtoList = api.getChatRooms()
        return dtoList.map {
            ChatRoomSummary(
                id = it.id,
                title = it.title,
                lastMessage = it.lastMessage,
                createdAt = it.createdAt
            )
        }
    }

    // 2) 특정 채팅방 전체 메시지 불러오기
    override suspend fun fetchChatRoom(roomId: String): ChatRoom? {
        // messages만 반환하는 API 사용
        val messagesDto = api.getMessages(roomId)

        val messages = messagesDto.map {
            ChatMessage(
                id = it.id,
                text = it.text,
                sender = if (it.sender == "USER") SenderType.USER else SenderType.BOT,
                timestamp = it.timestamp
            )
        }.toMutableList()

        return ChatRoom(
            id = roomId,
            title = "대화방",
            lastMessage = messages.lastOrNull()?.text ?: "",
            messages = messages
        )
    }

    // 3) 메시지 전송 (USER → BOT)
    override suspend fun sendUserMessage(roomId: String, userMessage: String): List<ChatMessage> {

        val request = BotRequestDto(
            text = userMessage
        )

        val response = api.sendMessage(roomId, request)

        return response.messages.map {
            ChatMessage(
                id = it.id,
                text = it.text,
                sender = if (it.sender == "USER") SenderType.USER else SenderType.BOT,
                timestamp = it.timestamp
            )
        }
    }
}
