package com.example.baro.feature.party.data.mapper

import com.example.baro.feature.party.data.model.CreatePartyRequestDto
import com.example.baro.feature.party.data.model.PartyDto
import com.example.baro.feature.party.data.model.PartyMemberDto
import com.example.baro.feature.party.domain.model.CreateParty
import com.example.baro.feature.party.domain.model.PartyDetail
import com.example.baro.feature.party.domain.model.PartyMember
import com.example.baro.feature.party.domain.model.PartyMemberRole
import com.example.baro.feature.party.domain.model.PartyMemberStatus
import com.example.baro.feature.party.domain.model.PartyStatus
import com.example.baro.feature.party.domain.model.PartySummary

object PartyMapper {

    // 상태 문자열 -> enum
    private fun String.toPartyStatus(): PartyStatus =
        when (lowercase()) {
            "scheduled" -> PartyStatus.SCHEDULED
            "completed" -> PartyStatus.COMPLETED
            "cancelled" -> PartyStatus.CANCELLED
            else -> PartyStatus.SCHEDULED
        }

    private fun String.toMemberRole(): PartyMemberRole =
        when (lowercase()) {
            "host" -> PartyMemberRole.HOST
            else -> PartyMemberRole.MEMBER
        }

    private fun String.toMemberStatus(): PartyMemberStatus =
        when (lowercase()) {
            "joined" -> PartyMemberStatus.JOINED
            "left" -> PartyMemberStatus.LEFT
            "kicked" -> PartyMemberStatus.KICKED
            else -> PartyMemberStatus.JOINED
        }

    // MemberDto -> Domain
    private fun PartyMemberDto.toDomain(): PartyMember =
        PartyMember(
            partyId = partyId,
            userId = userId,
            nickname = nickname,
            role = role.toMemberRole(),
            status = status.toMemberStatus(),
            joinedAt = joinedAt,
            sportsmanship = sportsmanship
        )

    // PartyDto -> PartySummary (목록/참여예정 카드용)
    fun PartyDto.toSummary(): PartySummary =
        PartySummary(
            partyId = partyId,
            title = title,
            sport = sport,
            place = place,
            description = description,
            date = date,
            startTime = startTime,
            endTime = endTime,
            capacity = capacity,
            current = current,
            hostId = hostId,
            status = status.toPartyStatus(),
            isJoined = isJoined,
            createdAt = createdAt
        )

    // PartyDto -> PartyDetail (members 포함)
    fun PartyDto.toDetail(): PartyDetail =
        PartyDetail(
            partyId = partyId,
            title = title,
            sport = sport,
            place = place,
            description = description,
            date = date,
            startTime = startTime,
            endTime = endTime,
            capacity = capacity,
            current = current,
            hostId = hostId,
            status = status.toPartyStatus(),
            isJoined = isJoined,
            createdAt = createdAt,
            placeLat = placeLat,
            placeLng = placeLng,
            members = members.map { it.toDomain() }
        )

    // CreateParty (domain) -> CreatePartyRequestDto (API 요청 바디)
    fun CreateParty.toRequestDto(): CreatePartyRequestDto =
        CreatePartyRequestDto(
            title = title,
            sport = sport,
            place = place,
            description = description,
            date = date,
            startTime = startTime,
            endTime = endTime,
            capacity = capacity
        )
}
