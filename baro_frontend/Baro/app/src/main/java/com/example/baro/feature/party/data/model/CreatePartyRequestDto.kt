package com.example.baro.feature.party.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
data class CreatePartyRequestDto(
    @SerialName("title")
    val title: String,

    @SerialName("sport")
    val sport: String,

    @SerialName("place")
    val place: String,

    @SerialName("description")
    val description: String,

    @SerialName("date")
    val date: String,

    @SerialName("start_time")
    val startTime: String,

    @SerialName("end_time")
    val endTime: String,

    @SerialName("capacity")
    val capacity: Int,

    @SerialName("place_lat")
    val placeLat: Double? = null,

    @SerialName("place_lng")
    val placeLng: Double? = null
)
