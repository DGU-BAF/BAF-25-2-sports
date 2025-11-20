// com/example/baro/feature/feedback/domain/usecase/GetMyPartiesForFeedbackUseCase.kt
package com.example.baro.feature.feedback.domain.usecase

import com.example.baro.feature.feedback.domain.model.MyPartyFeedback
import com.example.baro.feature.feedback.domain.repository.FeedbackRepository

class GetMyPartiesForFeedbackUseCase(
    private val repository: FeedbackRepository
) {
    suspend operator fun invoke(): List<MyPartyFeedback> {
        return repository.getMyPartiesForFeedback()
    }
}
