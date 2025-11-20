package com.example.baro.feature.party.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
data class PartyDto(
    @SerialName("partyId")
    val partyId: String,

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

    @SerialName("startTime")
    val startTime: String,

    @SerialName("endTime")
    val endTime: String,

    @SerialName("capacity")
    val capacity: Int,

    @SerialName("current")
    val current: Int,

    @SerialName("hostId")
    val hostId: String,

    @SerialName("status")
    val status: String,

    @SerialName("members")
    val members: List<PartyMemberDto> = emptyList(),

    @SerialName("isJoined")
    val isJoined: Boolean,

    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("placeLat")
    val placeLat: Double? = null,

    @SerialName("placeLng")
    val placeLng: Double? = null
)

