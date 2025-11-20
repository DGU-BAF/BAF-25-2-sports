package com.example.baro.feature.party.domain.model

// member.status 와 매핑 (joined / left / kicked 등)
enum class PartyMemberStatus {
    JOINED,   // 현재 참여 중
    LEFT,     // 스스로 나감
    KICKED    // 강퇴됨 (필요 없으면 안 써도 됨)
}
