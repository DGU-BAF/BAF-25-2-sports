// com/example/baro/feature/feedback/domain/model/MyPartyFeedback.kt
package com.example.baro.feature.feedback.domain.model

data class MyPartyFeedback(
    val partyId: String,
    val title: String,
    val date: String,          // "2025-12-12"
    val endAt: String,         // "2025-12-12T18:00:00" 같은 종료 시각
    val feedbackStatus: FeedbackStatus
)
