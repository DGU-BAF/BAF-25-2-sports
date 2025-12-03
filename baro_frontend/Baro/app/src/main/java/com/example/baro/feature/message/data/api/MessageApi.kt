package com.example.baro.feature.message.data.api

import com.example.baro.feature.message.data.dto.MessageDto
import com.example.baro.feature.message.data.dto.MessageRoomSummaryDto
import com.example.baro.feature.message.data.dto.SendMessageRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageApi {


//     파티별 마지막 메시지 목록 가져오기
//     GET /messages/rooms
    @GET("messages/rooms")
    suspend fun getMessageRooms(): List<MessageRoomSummaryDto>

//    특정 채팅방 전체 메시지 가져오기
//    GET /messages/{roomId}

    @GET("messages/{roomId}")
    suspend fun getMessages(
        @Path("roomId") roomId: String
    ): List<MessageDto>

//     메시지 전송
//     POST /messages
    @POST("messages")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    )
}
