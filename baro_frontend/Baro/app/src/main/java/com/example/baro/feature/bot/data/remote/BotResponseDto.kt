package com.example.baro.feature.bot.data.remote

import com.google.gson.annotations.SerializedName

data class BotResponseDto(
    @SerializedName("messages")
    val messages: List<ChatMessageDto>
)
