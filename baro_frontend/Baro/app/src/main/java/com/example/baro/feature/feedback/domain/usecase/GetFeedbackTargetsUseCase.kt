// com/example/baro/feature/feedback/domain/usecase/GetFeedbackTargetsUseCase.kt
package com.example.baro.feature.feedback.domain.usecase

import com.example.baro.feature.feedback.domain.model.FeedbackTargetMember
import com.example.baro.feature.feedback.domain.repository.FeedbackRepository

class GetFeedbackTargetsUseCase(
    private val repository: FeedbackRepository
) {
    suspend operator fun invoke(partyId: String): List<FeedbackTargetMember> {
        return repository.getFeedbackTargets(partyId)
    }
}
