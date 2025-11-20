package com.example.baro.feature.bot.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BotApiService {

    // 1) 챗봇 채팅방 목록
    @GET("bot/rooms")
    suspend fun getChatRooms(): List<ChatRoomSummaryDto>

    // 2) 특정 채팅방 메시지 목록
    @GET("bot/rooms/{roomId}/messages")
    suspend fun getMessages(
        @Path("roomId") roomId: String
    ): List<ChatMessageDto>

    // 3) 특정 채팅방에 메시지 전송 (USER → BOT)
    @POST("bot/rooms/{roomId}/messages")
    suspend fun sendMessage(
        @Path("roomId") roomId: String,
        @Body request: BotRequestDto
    ): BotResponseDto
}
