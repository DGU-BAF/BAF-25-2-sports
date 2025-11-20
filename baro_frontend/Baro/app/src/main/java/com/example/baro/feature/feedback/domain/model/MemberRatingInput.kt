// com/example/baro/feature/feedback/domain/model/MemberRatingInput.kt
package com.example.baro.feature.feedback.domain.model

data class MemberRatingInput(
    val userId: String,
    val rating: Int  // 1~5
)
