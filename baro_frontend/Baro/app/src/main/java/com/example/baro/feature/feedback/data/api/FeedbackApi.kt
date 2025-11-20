// feature/feedback/data/api/FeedbackApi.kt
package com.example.baro.feature.feedback.data.api

import com.example.baro.feature.feedback.data.model.FeedbackTargetDto
import com.example.baro.feature.feedback.data.model.MyPartyFeedbackDto
import com.example.baro.feature.feedback.data.model.SubmitFeedbackRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FeedbackApi {

    // 내가 참여한 파티 + 피드백 상태
    @GET("feedback/my-parties")
    suspend fun getMyParties(): List<MyPartyFeedbackDto>

    // 특정 파티에 대한 평가 대상 멤버
    @GET("feedback/parties/{partyId}/targets")
    suspend fun getFeedbackTargets(
        @Path("partyId") partyId: String
    ): List<FeedbackTargetDto>

    // 특정 파티에 대한 피드백 제출
    @POST("feedback/parties/{partyId}")
    suspend fun submitFeedback(
        @Path("partyId") partyId: String,
        @Body body: SubmitFeedbackRequestDto
    )
}
