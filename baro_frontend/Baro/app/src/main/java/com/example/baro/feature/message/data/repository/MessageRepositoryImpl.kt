package com.example.baro.feature.message.data.repository

import com.example.baro.feature.message.data.dto.MessageDto
import com.example.baro.feature.message.data.dto.MessageRoomSummaryDto
import com.example.baro.feature.message.data.dto.SendMessageRequest
import com.example.baro.feature.message.data.source.MessageRemoteSource
import com.example.baro.feature.message.domain.model.Message
import com.example.baro.feature.message.domain.model.MessageRoomSummary
import com.example.baro.feature.message.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class MessageRepositoryImpl(
    private val remoteSource: MessageRemoteSource,
    /**
     * 현재 로그인한 유저 ID 제공자
     * 예: { authManager.currentUserId }
     */
    private val currentUserIdProvider: () -> String
) : MessageRepository {

    override suspend fun getMessageRooms(): List<MessageRoomSummary> {
        return remoteSource
            .getMessageRooms()
            .map { it.toDomain() }
    }

    override suspend fun getMessages(roomId: String): List<Message> {
        val currentUserId = currentUserIdProvider()
        return remoteSource
            .getMessages(roomId)
            .map { it.toDomain(currentUserId) }
    }

    override suspend fun sendMessage(roomId: String, content: String) {
        val request = SendMessageRequest(
            roomId = roomId,
            content = content
        )
        remoteSource.sendMessage(request)
    }

    override fun observeMessages(roomId: String): Flow<Message> {
        val currentUserId = currentUserIdProvider()
        return remoteSource
            .observeMessages(roomId)
            .map { dto -> dto.toDomain(currentUserId) }
    }

    // -----------------------------
    // DTO -> Domain 매핑 함수들
    // -----------------------------

    private fun MessageDto.toDomain(currentUserId: String): Message {
        return Message(
            id = id,
            roomId = roomId,
            senderId = senderId,
            senderName = senderName,
            content = content,
            createdAt = parseDateTime(createdAt),
            isMine = senderId == currentUserId
        )
    }

    private fun MessageRoomSummaryDto.toDomain(): MessageRoomSummary {
        return MessageRoomSummary(
            roomId = roomId,
            roomName = roomName,
            lastMessage = lastMessage,
            lastMessageTime = parseDateTime(lastMessageTime)
        )
    }

    /**
     * 서버에서 ISO 8601 문자열(예: "2025-11-20T02:30:00")을 내려준다고 가정
     * 포맷이 다르면 이 부분만 수정하면 됩니다.
     */
    private fun parseDateTime(value: String): LocalDateTime {
        return LocalDateTime.parse(value)
        // 필요하면 DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") 등으로 커스터마이즈
    }
}
