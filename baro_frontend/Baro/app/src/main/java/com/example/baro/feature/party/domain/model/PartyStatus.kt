package com.example.baro.feature.party.domain.model

// DB status 컬럼: "scheduled", "completed", "cancelled" 등과 매핑해서 사용
enum class PartyStatus {
    SCHEDULED,   // 진행 예정
    COMPLETED,   // 완료됨
    CANCELLED    // 취소됨
}
