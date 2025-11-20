package com.example.baro.feature.party.domain.model

data class PartyMember(
    val partyId: String,              // party_id
    val userId: String,
    val nickname: String,
    val role: PartyMemberRole,          // HOST / MEMBER
    val status: PartyMemberStatus,      // JOINED / LEFT / KICKED
    val joinedAt: String?,              // "2025-11-18T10:00:00" 같은 형태 (필요시)
    val sportsmanship: Int?             // 매너온도 (예: 40, 50) - 없으면 null
)
