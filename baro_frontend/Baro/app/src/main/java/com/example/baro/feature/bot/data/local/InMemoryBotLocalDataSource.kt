package com.example.baro.feature.bot.data.local

import com.example.baro.feature.bot.domain.model.ChatRoom
import com.example.baro.feature.bot.domain.model.ChatRoomSummary

class InMemoryBotLocalDataSource : BotLocalDataSource {

    private val rooms: MutableMap<String, ChatRoom> = LinkedHashMap()

    override suspend fun loadChatRooms(): List<ChatRoomSummary> {
        return rooms.values
            .sortedByDescending { it.createdAt }
            .map { room ->
                ChatRoomSummary(
                    id = room.id,
                    title = room.title,
                    lastMessage = room.lastMessage,
                    createdAt = room.createdAt
                )
            }
    }

    override suspend fun loadChatRoom(roomId: String): ChatRoom? {
        return rooms[roomId]
    }

    override suspend fun saveChatRoom(room: ChatRoom) {
        rooms[room.id] = room
    }

    override suspend fun createNewRoom(room: ChatRoom) {
        rooms[room.id] = room
    }

    override suspend fun updateRoomTitle(roomId: String, newTitle: String) {
        val room = rooms[roomId] ?: return
        rooms[roomId] = room.copy(title = newTitle)
    }
}
