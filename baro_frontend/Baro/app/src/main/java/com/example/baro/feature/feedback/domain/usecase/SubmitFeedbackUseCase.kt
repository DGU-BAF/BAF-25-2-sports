// com/example/baro/feature/feedback/domain/usecase/SubmitFeedbackUseCase.kt
package com.example.baro.feature.feedback.domain.usecase

import com.example.baro.feature.feedback.domain.model.MemberRatingInput
import com.example.baro.feature.feedback.domain.repository.FeedbackRepository

class SubmitFeedbackUseCase(
    private val repository: FeedbackRepository
) {
    suspend operator fun invoke(
        partyId: String,
        ratings: List<MemberRatingInput>
    ) {
        repository.submitFeedback(partyId, ratings)
    }
}
