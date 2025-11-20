package com.example.baro.feature.party.data.repository

import com.example.baro.feature.party.data.mapper.PartyMapper.toDetail
import com.example.baro.feature.party.data.mapper.PartyMapper.toRequestDto
import com.example.baro.feature.party.data.mapper.PartyMapper.toSummary
import com.example.baro.feature.party.data.source.PartyRemoteDataSource
import com.example.baro.feature.party.domain.model.CreateParty
import com.example.baro.feature.party.domain.model.PartyDetail
import com.example.baro.feature.party.domain.model.PartySummary
import com.example.baro.feature.party.domain.repository.PartyRepository

class PartyRepositoryImpl(
    private val remoteDataSource: PartyRemoteDataSource
) : PartyRepository {

    override suspend fun getPartyList(): List<PartySummary> {
        return remoteDataSource
            .getPartyList()
            .map { it.toSummary() }
    }

    override suspend fun getUpcomingJoinedParties(): List<PartySummary> {
        return remoteDataSource
            .getUpcomingJoinedParties()
            .map { it.toSummary() }
    }

    override suspend fun getPartyDetail(partyId: String): PartyDetail {
        return remoteDataSource
            .getPartyDetail(partyId)
            .toDetail()
    }

    override suspend fun createParty(request: CreateParty): PartyDetail {
        val dto = remoteDataSource.createParty(request.toRequestDto())
        return dto.toDetail()
    }

    override suspend fun joinParty(partyId: String): PartyDetail {
        val dto = remoteDataSource.joinParty(partyId)
        return dto.toDetail()
    }

    override suspend fun leaveParty(partyId: String): PartyDetail {
        val dto = remoteDataSource.leaveParty(partyId)
        return dto.toDetail()
    }
}
