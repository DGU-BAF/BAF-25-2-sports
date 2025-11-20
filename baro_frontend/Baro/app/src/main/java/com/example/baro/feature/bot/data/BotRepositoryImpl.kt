package com.example.baro.feature.bot.data

import com.example.baro.feature.bot.data.local.BotLocalDataSource
import com.example.baro.feature.bot.data.remote.BotRemoteDataSource
import com.example.baro.feature.bot.domain.model.ChatMessage
import com.example.baro.feature.bot.domain.model.ChatRoom
import com.example.baro.feature.bot.domain.model.ChatRoomSummary
import com.example.baro.feature.bot.domain.model.SenderType
import java.util.UUID

class BotRepositoryImpl(
    private val localDataSource: BotLocalDataSource,
    private val remoteDataSource: BotRemoteDataSource
) : BotRepository {

    // 1) 채팅방 목록: 이제 로컬이 아니라 백엔드에서 가져오기
    override suspend fun getChatRooms(): List<ChatRoomSummary> {
        // remoteDataSource.fetchChatRooms() 는
        // 백엔드 /bot/rooms → List<ChatRoomSummaryDto> 호출한다고 가정
        return remoteDataSource.fetchChatRooms()
    }

    // 2) 특정 채팅방: 마찬가지로 백엔드에서 가져오기
    override suspend fun getChatRoom(roomId: String): ChatRoom? {
        return remoteDataSource.fetchChatRoom(roomId)
    }

    // 3) 새 채팅방 만들기
    override suspend fun createChatRoom(initialMessage: String?): ChatRoom {
        // 이 부분은 아직 백엔드에 "방 생성" API가 없으니까
        // 로컬에서만 UUID로 방 하나 만든 다음, 필요하면 나중에 서버 연동
        val roomId = UUID.randomUUID().toString()

        val room = ChatRoom(
            id = roomId,
            title = "새 대화",
            lastMessage = "",
            messages = mutableListOf()
        )

        localDataSource.createNewRoom(room)

        // 초기 메시지가 있으면 바로 sendUserMessage 로직 재사용
        return if (!initialMessage.isNullOrBlank()) {
            sendUserMessage(
                roomId = roomId,
                messageText = initialMessage
            )
        } else {
            room
        }
    }

    // 4) 메시지 전송: 이제 remote를 통해 전체 메시지까지 업데이트
    override suspend fun sendUserMessage(roomId: String, messageText: String): ChatRoom {
        // remoteDataSource가 백엔드에 POST /bot/rooms/{roomId}/messages 호출해서
        // "user + bot 메시지"를 받아온다고 가정
        val updatedMessages: List<ChatMessage> =
            remoteDataSource.sendUserMessage(roomId = roomId, userMessage = messageText)

        // 도메인 모델 ChatRoom은 messages 전체를 가지고 있어야 하므로,
        // 여기서는 "방 정보"를 다시 가져오거나, 로컬에 저장된 방을 업데이트하는 식으로 처리
        val existingRoom = localDataSource.loadChatRoom(roomId)
            ?: ChatRoom(
                id = roomId,
                title = "새 대화",
                lastMessage = updatedMessages.lastOrNull()?.text.orEmpty(),
                messages = mutableListOf()
            )

        existingRoom.messages.clear()
        existingRoom.messages.addAll(updatedMessages)
        existingRoom.lastMessage = updatedMessages.lastOrNull()?.text.orEmpty()

        localDataSource.saveChatRoom(existingRoom)

        return existingRoom
    }

    override suspend fun updateRoomTitle(roomId: String, newTitle: String) {
        localDataSource.updateRoomTitle(roomId, newTitle)
    }
}
