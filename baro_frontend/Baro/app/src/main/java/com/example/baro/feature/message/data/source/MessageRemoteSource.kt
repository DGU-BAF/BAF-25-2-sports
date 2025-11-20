package com.example.baro.feature.message.data.source

import com.example.baro.feature.message.data.dto.MessageDto
import com.example.baro.feature.message.data.dto.MessageRoomSummaryDto
import com.example.baro.feature.message.data.dto.SendMessageRequest
import kotlinx.coroutines.flow.Flow

interface MessageRemoteSource {

    suspend fun getMessageRooms(): List<MessageRoomSummaryDto>

    suspend fun getMessages(roomId: String): List<MessageDto>

    suspend fun sendMessage(request: SendMessageRequest)

    /**
     * 실시간 메시지 스트림 (Supabase Realtime / WebSocket 등)
     * 우선 인터페이스만 정의해두고, 구현은 나중에 채우면 됩니다.
     */
    fun observeMessages(roomId: String): Flow<MessageDto>
}
