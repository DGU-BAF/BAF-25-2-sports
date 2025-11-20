package com.example.baro.feature.message.data.api

class MessageApiImpl(
    private val api: MessageApi
) : MessageApi {

    override suspend fun getMessageRooms() =
        api.getMessageRooms()

    override suspend fun getMessages(roomId: String) =
        api.getMessages(roomId)

    override suspend fun sendMessage(request: com.example.baro.feature.message.data.dto.SendMessageRequest) =
        api.sendMessage(request)
}
