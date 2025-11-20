// feature/feedback/data/model/SubmitFeedbackRequestDto.kt
package com.example.baro.feature.feedback.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
data class SubmitFeedbackRequestDto(
    @SerialName("party_id") val partyId: String,
    @SerialName("ratings") val ratings: List<MemberRatingDto>
)

//@Serializable
data class MemberRatingDto(
    @SerialName("user_id") val userId: String,
    @SerialName("rating") val rating: Int
)
