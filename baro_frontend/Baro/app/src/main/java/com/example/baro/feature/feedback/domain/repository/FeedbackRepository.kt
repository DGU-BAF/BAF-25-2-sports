// com/example/baro/feature/feedback/domain/repository/FeedbackRepository.kt
package com.example.baro.feature.feedback.domain.repository

import com.example.baro.feature.feedback.domain.model.FeedbackTargetMember
import com.example.baro.feature.feedback.domain.model.MemberRatingInput
import com.example.baro.feature.feedback.domain.model.MyPartyFeedback

interface FeedbackRepository {

    // 설정 > 참여한 파티 목록
    suspend fun getMyPartiesForFeedback(): List<MyPartyFeedback>

    // 특정 파티에 대해 평가해야 할 대상 멤버 목록
    suspend fun getFeedbackTargets(partyId: String): List<FeedbackTargetMember>

    // 특정 파티에 대한 멤버별 별점 제출
    suspend fun submitFeedback(
        partyId: String,
        ratings: List<MemberRatingInput>
    )
}
