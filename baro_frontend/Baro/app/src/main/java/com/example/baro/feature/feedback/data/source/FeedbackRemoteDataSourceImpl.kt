// feature/feedback/data/source/FeedbackRemoteDataSourceImpl.kt
package com.example.baro.feature.feedback.data.source

import com.example.baro.feature.feedback.data.api.FeedbackApi
import com.example.baro.feature.feedback.data.model.FeedbackTargetDto
import com.example.baro.feature.feedback.data.model.MyPartyFeedbackDto
import com.example.baro.feature.feedback.data.model.SubmitFeedbackRequestDto

class FeedbackRemoteDataSourceImpl(
    private val api: FeedbackApi
) : FeedbackRemoteDataSource {

    override suspend fun getMyParties(): List<MyPartyFeedbackDto> =
        api.getMyParties()

    override suspend fun getFeedbackTargets(partyId: String): List<FeedbackTargetDto> =
        api.getFeedbackTargets(partyId)

    override suspend fun submitFeedback(
        partyId: String,
        body: SubmitFeedbackRequestDto
    ) {
        api.submitFeedback(partyId, body)
    }
}
