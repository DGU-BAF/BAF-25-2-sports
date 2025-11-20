// feature/feedback/ui/rating/RatingMemberUiModel.kt
package com.example.baro.feature.feedback.ui.rating

import com.example.baro.feature.feedback.domain.model.FeedbackTargetMember

data class RatingMemberUiModel(
    val userId: String,
    val nickname: String,
    val rating: Int
) {
    companion object {
        fun from(domain: FeedbackTargetMember): RatingMemberUiModel =
            RatingMemberUiModel(
                userId = domain.userId,
                nickname = domain.nickname,
                rating = 3 // 기본 3점
            )
    }
}
