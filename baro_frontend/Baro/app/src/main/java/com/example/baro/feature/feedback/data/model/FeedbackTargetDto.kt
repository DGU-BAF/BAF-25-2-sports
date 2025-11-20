// feature/feedback/data/model/FeedbackTargetDto.kt
package com.example.baro.feature.feedback.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
data class FeedbackTargetDto(
    @SerialName("user_id") val userId: String,
    @SerialName("nickname") val nickname: String,
    @SerialName("sportsmanship") val sportsmanship: Int? = null
)
