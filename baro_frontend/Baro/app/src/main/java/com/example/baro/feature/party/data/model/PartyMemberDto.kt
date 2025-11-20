package com.example.baro.feature.party.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
data class PartyMemberDto(
    @SerialName("party_id")
    val partyId: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("nickname")
    val nickname: String,

    @SerialName("role")
    val role: String,          // "host" / "member"

    @SerialName("status")
    val status: String,        // "joined" / "left" / "kicked"

    @SerialName("joined_at")
    val joinedAt: String,

    @SerialName("sportsmanship")
    val sportsmanship: Int? = null
)
