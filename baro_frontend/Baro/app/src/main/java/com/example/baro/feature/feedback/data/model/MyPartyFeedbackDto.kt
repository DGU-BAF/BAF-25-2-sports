// feature/feedback/data/model/MyPartyFeedbackDto.kt
package com.example.baro.feature.feedback.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
data class MyPartyFeedbackDto(
    @SerialName("party_id") val partyId: String,
    @SerialName("title") val title: String,
    @SerialName("date") val date: String,          // "2025-12-12"
    @SerialName("end_at") val endAt: String,       // "2025-12-12T18:00:00"
    @SerialName("feedback_status") val feedbackStatus: String // "available" / "submitted" / "expired"
)
