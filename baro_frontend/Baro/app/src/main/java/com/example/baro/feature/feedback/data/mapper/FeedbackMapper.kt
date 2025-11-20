// feature/feedback/data/mapper/FeedbackMapper.kt
package com.example.baro.feature.feedback.data.mapper

import com.example.baro.feature.feedback.data.model.FeedbackTargetDto
import com.example.baro.feature.feedback.data.model.MemberRatingDto
import com.example.baro.feature.feedback.data.model.MyPartyFeedbackDto
import com.example.baro.feature.feedback.domain.model.FeedbackStatus
import com.example.baro.feature.feedback.domain.model.FeedbackTargetMember
import com.example.baro.feature.feedback.domain.model.MemberRatingInput
import com.example.baro.feature.feedback.domain.model.MyPartyFeedback

fun MyPartyFeedbackDto.toDomain(): MyPartyFeedback {
    val status = when (feedbackStatus.lowercase()) {
        "available" -> FeedbackStatus.AVAILABLE
        "submitted" -> FeedbackStatus.SUBMITTED
        "expired" -> FeedbackStatus.EXPIRED
        else -> FeedbackStatus.EXPIRED
    }
    return MyPartyFeedback(
        partyId = partyId,
        title = title,
        date = date,
        endAt = endAt,
        feedbackStatus = status
    )
}

fun FeedbackTargetDto.toDomain(): FeedbackTargetMember =
    FeedbackTargetMember(
        userId = userId,
        nickname = nickname,
        currentSportsmanship = sportsmanship
    )

fun MemberRatingInput.toDto(): MemberRatingDto =
    MemberRatingDto(
        userId = userId,
        rating = rating
    )
