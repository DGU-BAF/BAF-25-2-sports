package com.example.baro.feature.bot.data.remote

import com.google.gson.annotations.SerializedName

data class BotRequestDto(
    @SerializedName("text")
    val text: String
)
