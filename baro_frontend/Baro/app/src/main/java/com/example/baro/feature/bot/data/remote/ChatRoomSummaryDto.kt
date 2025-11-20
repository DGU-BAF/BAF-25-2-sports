package com.example.baro.feature.bot.data.remote

import com.google.gson.annotations.SerializedName

data class ChatRoomSummaryDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("lastMessage")
    val lastMessage: String,

    @SerializedName("createdAt")
    val createdAt: Long
)
