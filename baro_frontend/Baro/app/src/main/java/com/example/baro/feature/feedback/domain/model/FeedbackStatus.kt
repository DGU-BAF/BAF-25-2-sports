// com/example/baro/feature/feedback/domain/model/FeedbackStatus.kt
package com.example.baro.feature.feedback.domain.model

enum class FeedbackStatus {
    AVAILABLE,   // 피드백 가능 (종료 후 48시간 이내, 아직 안 함)
    SUBMITTED,   // 피드백 완료
    EXPIRED      // 피드백 기한 마감
}
