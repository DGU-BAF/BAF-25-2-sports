package com.example.baro.feature.message.data.source

import com.example.baro.feature.message.data.api.MessageApi
import com.example.baro.feature.message.data.dto.MessageDto
import com.example.baro.feature.message.data.dto.MessageRoomSummaryDto
import com.example.baro.feature.message.data.dto.SendMessageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MessageRemoteSourceImpl(
    private val api: MessageApi
) : MessageRemoteSource {

    override suspend fun getMessageRooms(): List<MessageRoomSummaryDto> {
        return api.getMessageRooms()
    }

    override suspend fun getMessages(roomId: String): List<MessageDto> {
        return api.getMessages(roomId)
    }

    override suspend fun sendMessage(request: SendMessageRequest) {
        api.sendMessage(request)
    }

    override fun observeMessages(roomId: String): Flow<MessageDto> {
        // TODO: Supabase Realtime / WebSocket 으로 교체
        return flow {
            // 현재는 구현하지 않고, 나중에 실시간 기능 붙일 때 채우면 됩니다.
        }
    }
}
