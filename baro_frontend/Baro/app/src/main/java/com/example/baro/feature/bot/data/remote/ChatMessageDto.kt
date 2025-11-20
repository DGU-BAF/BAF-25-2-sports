package com.example.baro.feature.bot.data.remote

import com.google.gson.annotations.SerializedName

data class ChatMessageDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("sender")
    val sender: String,     // "USER" or "BOT"

    @SerializedName("timestamp")
    val timestamp: Long
)
