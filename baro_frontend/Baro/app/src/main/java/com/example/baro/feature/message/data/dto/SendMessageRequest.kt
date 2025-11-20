package com.example.baro.feature.message.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
data class SendMessageRequest(
    @SerialName("room_id")
    val roomId: String,

    @SerialName("content")
    val content: String
)
