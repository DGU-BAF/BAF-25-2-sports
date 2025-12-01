// feature/bot/data/local/BotLocalDataSource.kt

package com.example.baro.feature.bot.data.local

import com.example.baro.feature.bot.domain.model.ChatRoom
import com.example.baro.feature.bot.domain.model.ChatRoomSummary

interface BotLocalDataSource {

    /** 전체 방 목록 (로컬 기준) */
    suspend fun loadChatRooms(): List<ChatRoomSummary>

    /** 특정 방 불러오기 */
    suspend fun loadChatRoom(roomId: String): ChatRoom?

    /** 특정 방 저장 */
    suspend fun saveChatRoom(room: ChatRoom)

    /** 방 생성 */
    suspend fun createNewRoom(room: ChatRoom)

    /** 방 제목 업데이트 */
    suspend fun updateRoomTitle(roomId: String, newTitle: String)

    /** 새로운 백엔드 room 목록을 로컬과 동기화 (RepositoryImpl에서 사용) */
    suspend fun saveChatRoomSummaries(rooms: List<ChatRoomSummary>)
}
