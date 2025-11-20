// feature/feedback/data/repository/FeedbackRepositoryImpl.kt
package com.example.baro.feature.feedback.data.repository

import com.example.baro.feature.feedback.data.mapper.toDomain
import com.example.baro.feature.feedback.data.mapper.toDto
import com.example.baro.feature.feedback.data.model.SubmitFeedbackRequestDto
import com.example.baro.feature.feedback.data.source.FeedbackRemoteDataSource
import com.example.baro.feature.feedback.domain.model.FeedbackTargetMember
import com.example.baro.feature.feedback.domain.model.MemberRatingInput
import com.example.baro.feature.feedback.domain.model.MyPartyFeedback
import com.example.baro.feature.feedback.domain.repository.FeedbackRepository

class FeedbackRepositoryImpl(
    private val remote: FeedbackRemoteDataSource
) : FeedbackRepository {

    override suspend fun getMyPartiesForFeedback(): List<MyPartyFeedback> {
        return remote.getMyParties().map { it.toDomain() }
    }

    override suspend fun getFeedbackTargets(partyId: String): List<FeedbackTargetMember> {
        return remote.getFeedbackTargets(partyId).map { it.toDomain() }
    }

    override suspend fun submitFeedback(
        partyId: String,
        ratings: List<MemberRatingInput>
    ) {
        val body = SubmitFeedbackRequestDto(
            partyId = partyId,
            ratings = ratings.map { it.toDto() }
        )
        remote.submitFeedback(partyId, body)
    }
}
