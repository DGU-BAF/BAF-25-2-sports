package com.example.baro.feature.party.data.source

import com.example.baro.feature.party.data.model.CreatePartyRequestDto
import com.example.baro.feature.party.data.model.PartyDto

class PartyRemoteDataSourceImpl(
    private val api: PartyApi
) : PartyRemoteDataSource {

    override suspend fun getPartyList(): List<PartyDto> {
        return api.getPartyList()
    }

    // 서버에 /party/upcoming 이 없으므로
    // 일단 전체 리스트에서 isJoined == true 인 것만 필터링
    override suspend fun getUpcomingJoinedParties(): List<PartyDto> {
        return api.getPartyList()
            .filter { it.isJoined }
    }

    override suspend fun getPartyDetail(partyId: String): PartyDto {
        return api.getPartyDetail(partyId)
    }

    override suspend fun createParty(request: CreatePartyRequestDto): PartyDto {
        return api.createParty(request)
    }

    override suspend fun joinParty(partyId: String): PartyDto {
        return api.joinParty(partyId)
    }

    override suspend fun leaveParty(partyId: String): PartyDto {
        return api.leaveParty(partyId)
    }
}
