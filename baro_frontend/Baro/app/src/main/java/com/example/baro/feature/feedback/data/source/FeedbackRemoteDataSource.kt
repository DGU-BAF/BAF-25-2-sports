// feature/feedback/data/source/FeedbackRemoteDataSource.kt
package com.example.baro.feature.feedback.data.source

import com.example.baro.feature.feedback.data.model.FeedbackTargetDto
import com.example.baro.feature.feedback.data.model.MyPartyFeedbackDto
import com.example.baro.feature.feedback.data.model.SubmitFeedbackRequestDto

interface FeedbackRemoteDataSource {
    suspend fun getMyParties(): List<MyPartyFeedbackDto>
    suspend fun getFeedbackTargets(partyId: String): List<FeedbackTargetDto>
    suspend fun submitFeedback(
        partyId: String,
        body: SubmitFeedbackRequestDto
    )
}
