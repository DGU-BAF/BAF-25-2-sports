// feature/bot/data/local/InMemoryBotLocalDataSource.kt

package com.example.baro.feature.bot.data.local

import com.example.baro.feature.bot.domain.model.ChatRoom
import com.example.baro.feature.bot.domain.model.ChatRoomSummary

class InMemoryBotLocalDataSource : BotLocalDataSource {

    /** 메시지 포함한 전체 ChatRoom 저장 */
    private val rooms: MutableMap<String, ChatRoom> = LinkedHashMap()

    /** 백엔드에서 받아온 요약 리스트 저장 */
    private var summaries: MutableMap<String, ChatRoomSummary> = LinkedHashMap()

    override suspend fun loadChatRooms(): List<ChatRoomSummary> {
        return summaries.values
            .sortedByDescending { it.createdAt }
            .toList()
    }

    override suspend fun saveChatRoomSummaries(rooms: List<ChatRoomSummary>) {
        summaries.clear()
        for (room in rooms) {
            summaries[room.id] = room
        }
    }

    override suspend fun loadChatRoom(roomId: String): ChatRoom? {
        return rooms[roomId]
    }

    override suspend fun saveChatRoom(room: ChatRoom) {
        rooms[room.id] = room

        // summary도 업데이트
        summaries[room.id] = ChatRoomSummary(
            id = room.id,
            title = room.title,
            lastMessage = room.lastMessage,
            createdAt = room.createdAt
        )
    }

    override suspend fun createNewRoom(room: ChatRoom) {
        rooms[room.id] = room
        summaries[room.id] = ChatRoomSummary(
            id = room.id,
            title = room.title,
            lastMessage = room.lastMessage,
            createdAt = room.createdAt
        )
    }

    override suspend fun updateRoomTitle(roomId: String, newTitle: String) {
        val room = rooms[roomId] ?: return
        val updated = room.copy(title = newTitle)
        rooms[roomId] = updated

        summaries[roomId] = summaries[roomId]?.copy(title = newTitle)
            ?: ChatRoomSummary(
                id = roomId,
                title = newTitle,
                lastMessage = updated.lastMessage,
                createdAt = updated.createdAt
            )
    }
}
