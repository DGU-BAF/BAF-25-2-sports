package com.example.baro.feature.party.ui.list

import com.example.baro.feature.party.domain.model.PartySummary
import com.example.baro.feature.party.domain.model.PartyStatus

data class PartyListItemUiModel(
    val summary: PartySummary,
    val actionText: String,
    val actionEnabled: Boolean,
) {
    companion object {
        fun from(summary: PartySummary): PartyListItemUiModel {
            val actionText = when {
                summary.isJoined -> "나가기"
                summary.isFull -> "참여 불가"
                else -> "참여하기"
            }

            val actionEnabled = when {
                !summary.isScheduled -> false   // 예정된 파티만 가능
                summary.isJoined -> true        // 나가기 가능
                summary.isFull -> false         // 참여 불가
                else -> true                    // 참여하기
            }

            return PartyListItemUiModel(
                summary = summary,
                actionText = actionText,
                actionEnabled = actionEnabled
            )
        }
    }
}
