package com.example.baro.feature.bot.data.local

import com.example.baro.feature.bot.domain.model.ChatRoom
import com.example.baro.feature.bot.domain.model.ChatRoomSummary

interface BotLocalDataSource {

    suspend fun loadChatRooms(): List<ChatRoomSummary>

    suspend fun loadChatRoom(roomId: String): ChatRoom?

    suspend fun saveChatRoom(room: ChatRoom)

    suspend fun createNewRoom(room: ChatRoom)

    suspend fun updateRoomTitle(roomId: String, newTitle: String)
}
