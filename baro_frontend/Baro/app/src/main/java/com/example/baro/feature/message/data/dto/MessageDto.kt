package com.example.baro.feature.message.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
data class MessageDto(
    @SerialName("id")
    val id: String,

    @SerialName("room_id")
    val roomId: String,

    @SerialName("sender_id")
    val senderId: String,

    @SerialName("sender_name")
    val senderName: String,

    @SerialName("content")
    val content: String,

    @SerialName("created_at")
    val createdAt: String
)
