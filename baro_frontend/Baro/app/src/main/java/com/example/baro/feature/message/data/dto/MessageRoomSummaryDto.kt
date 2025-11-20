package com.example.baro.feature.message.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
data class MessageRoomSummaryDto(
    @SerialName("room_id")
    val roomId: String,

    @SerialName("room_name")
    val roomName: String,

    @SerialName("last_message")
    val lastMessage: String,

    @SerialName("last_message_time")
    val lastMessageTime: String
)
