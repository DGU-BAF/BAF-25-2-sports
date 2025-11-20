package com.example.baro.feature.party.domain.usecase

import com.example.baro.feature.party.domain.model.PartyDetail
import com.example.baro.feature.party.domain.repository.PartyRepository

class GetPartyDetailUseCase(
    private val repository: PartyRepository
) {
    suspend operator fun invoke(partyId: String): PartyDetail {
        return repository.getPartyDetail(partyId)
    }
}
