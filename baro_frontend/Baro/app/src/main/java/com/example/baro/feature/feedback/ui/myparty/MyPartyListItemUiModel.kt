// feature/feedback/ui/myparty/MyPartyListItemUiModel.kt
package com.example.baro.feature.feedback.ui.myparty

import com.example.baro.feature.feedback.domain.model.FeedbackStatus
import com.example.baro.feature.feedback.domain.model.MyPartyFeedback

data class MyPartyListItemUiModel(
    val partyId: String,
    val title: String,
    val displayDate: String,
    val statusText: String,
    val canClickForFeedback: Boolean
) {
    companion object {
        fun from(domain: MyPartyFeedback): MyPartyListItemUiModel {
            val displayDate = formatDate(domain.date)
            val (statusText, clickable) = when (domain.feedbackStatus) {
                FeedbackStatus.AVAILABLE -> "피드백 가능" to true
                FeedbackStatus.SUBMITTED -> "피드백 완료" to false
                FeedbackStatus.EXPIRED -> "피드백 기한 마감" to false
            }
            return MyPartyListItemUiModel(
                partyId = domain.partyId,
                title = domain.title,
                displayDate = displayDate,
                statusText = statusText,
                canClickForFeedback = clickable
            )
        }

        private fun formatDate(date: String): String {
            // "2025-12-12" → "2025년 12월 12일"
            val parts = date.split("-")
            if (parts.size != 3) return date
            val y = parts[0].toIntOrNull() ?: return date
            val m = parts[1].toIntOrNull() ?: return date
            val d = parts[2].toIntOrNull() ?: return date
            return "${y}년 ${m}월 ${d}일"
        }
    }
}
