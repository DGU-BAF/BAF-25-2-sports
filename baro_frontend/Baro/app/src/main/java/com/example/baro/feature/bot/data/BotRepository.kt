// feature/bot/data/BotRepository.kt

package com.example.baro.feature.bot.data

import com.example.baro.feature.bot.domain.model.ChatRoom
import com.example.baro.feature.bot.domain.model.ChatRoomSummary

interface BotRepository {

    /** 전체 대화 목록 */
    suspend fun getChatRooms(): List<ChatRoomSummary>

    /** 방 하나 불러오기 (메시지 포함) */
    suspend fun getChatRoom(roomId: String): ChatRoom?

    /** 새 방 생성 (처음 메시지가 있으면 바로 전송까지 처리 가능) */
    suspend fun createChatRoom(initialMessage: String? = null): ChatRoom

    /** 유저 메시지 전송 + 서버(FastAPI)에서 Bot 응답 반영 후 ChatRoom 반환 */
    suspend fun sendUserMessage(roomId: String, messageText: String): ChatRoom

    /** 방 제목 수정 (로컬 전용) */
    suspend fun updateRoomTitle(roomId: String, newTitle: String)
}
