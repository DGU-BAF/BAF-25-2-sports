package com.example.baro.feature.party.domain.usecase

import com.example.baro.feature.party.domain.model.PartySummary
import com.example.baro.feature.party.domain.repository.PartyRepository

class GetPartyListUseCase(
    private val repository: PartyRepository
) {
    suspend operator fun invoke(): List<PartySummary> {
        return repository.getPartyList()
    }
}
