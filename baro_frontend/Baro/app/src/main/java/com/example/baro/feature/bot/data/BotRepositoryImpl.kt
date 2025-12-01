// feature/bot/data/BotRepositoryImpl.kt

package com.example.baro.feature.bot.data

import com.example.baro.feature.bot.data.local.BotLocalDataSource
import com.example.baro.feature.bot.data.remote.BotRemoteDataSource
import com.example.baro.feature.bot.domain.model.ChatMessage
import com.example.baro.feature.bot.domain.model.ChatRoom
import com.example.baro.feature.bot.domain.model.ChatRoomSummary
import java.util.UUID

class BotRepositoryImpl(
    private val localDataSource: BotLocalDataSource,
    private val remoteDataSource: BotRemoteDataSource
) : BotRepository {

    /** 1) 채팅방 목록: 백엔드 기준으로 가져오고 로컬에도 캐싱 */
    override suspend fun getChatRooms(): List<ChatRoomSummary> {
        val remoteRooms = remoteDataSource.fetchChatRooms()
        localDataSource.saveChatRoomSummaries(remoteRooms)
        return remoteRooms
    }

    /** 2) 특정 채팅방: 백엔드 메시지 기준으로 구성 + 로컬 캐싱 */
    override suspend fun getChatRoom(roomId: String): ChatRoom? {
        val remoteRoom = remoteDataSource.fetchChatRoom(roomId)
        if (remoteRoom != null) {
            localDataSource.saveChatRoom(remoteRoom)
        }
        return remoteRoom
    }

    /** 3) 새 채팅방 생성 (백엔드에 별도 방 생성 API 없으므로 UUID 기반 로컬/백엔드 공용 ID 사용) */
    override suspend fun createChatRoom(initialMessage: String?): ChatRoom {
        val roomId = UUID.randomUUID().toString()

        // 일단 빈 방을 로컬에 만들어둔다
        val emptyRoom = ChatRoom(
            id = roomId,
            title = "새 대화",
            lastMessage = "",
            messages = mutableListOf()
        )
        localDataSource.createNewRoom(emptyRoom)

        // 초기 메시지가 있으면 바로 한 줄 보내고 최신 상태를 받아온다
        return if (!initialMessage.isNullOrBlank()) {
            sendUserMessage(roomId = roomId, messageText = initialMessage)
        } else {
            emptyRoom
        }
    }

    /** 4) 메시지 전송 후 최신 ChatRoom 반환 */
    override suspend fun sendUserMessage(roomId: String, messageText: String): ChatRoom {
        val updatedMessages: List<ChatMessage> =
            remoteDataSource.sendUserMessage(roomId = roomId, userMessage = messageText)

        // 로컬에 기존 방이 있으면 가져오고, 없으면 기본 방 생성
        val baseRoom = localDataSource.loadChatRoom(roomId) ?: ChatRoom(
            id = roomId,
            title = "새 대화",
            lastMessage = "",
            messages = mutableListOf()
        )

        val lastText = updatedMessages.lastOrNull()?.text.orEmpty()

        val updatedRoom = baseRoom.copy(
            lastMessage = lastText,
            messages = updatedMessages.toMutableList()
        )

        localDataSource.saveChatRoom(updatedRoom)
        return updatedRoom
    }

    /** 5) 방 제목 수정은 로컬만 건드리면 됨 (백엔드는 아직 없음) */
    override suspend fun updateRoomTitle(roomId: String, newTitle: String) {
        localDataSource.updateRoomTitle(roomId, newTitle)
    }
}
