package com.example.baro.feature.party.domain.repository

import com.example.baro.feature.party.domain.model.CreateParty
import com.example.baro.feature.party.domain.model.PartyDetail
import com.example.baro.feature.party.domain.model.PartySummary

interface PartyRepository {

    // 메인 목록 + 새로고침에서 사용
    suspend fun getPartyList(): List<PartySummary>

    // 홈 상단 "참여 예정 파티"
    suspend fun getUpcomingJoinedParties(): List<PartySummary>

    // 상세
    suspend fun getPartyDetail(partyId: String): PartyDetail

    // 생성
    suspend fun createParty(request: CreateParty): PartyDetail

    // 참여
    suspend fun joinParty(partyId: String): PartyDetail

    // 나가기 (방장은 서버에서 막거나 에러 반환)
    suspend fun leaveParty(partyId: String): PartyDetail
}
