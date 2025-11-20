package com.example.baro.feature.message.domain.repository

import com.example.baro.feature.message.domain.model.Message
import com.example.baro.feature.message.domain.model.MessageRoomSummary
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun getMessageRooms(): List<MessageRoomSummary>

    suspend fun getMessages(roomId: String): List<Message>

    suspend fun sendMessage(
        roomId: String,
        content: String
    )

    fun observeMessages(roomId: String): Flow<Message>
}
