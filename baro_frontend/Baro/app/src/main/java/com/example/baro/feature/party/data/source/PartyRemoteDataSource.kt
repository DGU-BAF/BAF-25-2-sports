package com.example.baro.feature.party.data.source

import com.example.baro.feature.party.data.model.CreatePartyRequestDto
import com.example.baro.feature.party.data.model.PartyDto

interface PartyRemoteDataSource {

    suspend fun getPartyList(): List<PartyDto>

    suspend fun getUpcomingJoinedParties(): List<PartyDto>

    suspend fun getPartyDetail(partyId: String): PartyDto

    suspend fun createParty(request: CreatePartyRequestDto): PartyDto

    suspend fun joinParty(partyId: String): PartyDto

    suspend fun leaveParty(partyId: String): PartyDto
}
