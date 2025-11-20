// com/example/baro/feature/feedback/domain/model/FeedbackTargetMember.kt
package com.example.baro.feature.feedback.domain.model

data class FeedbackTargetMember(
    val userId: String,
    val nickname: String,
    val currentSportsmanship: Int? // 기존 매너온도, 없으면 null
)
