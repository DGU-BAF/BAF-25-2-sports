package com.example.baro.feature.bot.data.remote

import com.example.baro.feature.bot.domain.model.ChatMessage
import com.example.baro.feature.bot.domain.model.ChatRoom
import com.example.baro.feature.bot.domain.model.ChatRoomSummary

interface BotRemoteDataSource {
    suspend fun fetchChatRooms(): List<ChatRoomSummary>
    suspend fun fetchChatRoom(roomId: String): ChatRoom?
    suspend fun sendUserMessage(roomId: String, userMessage: String): List<ChatMessage>

}
