package com.example.baro.feature.party.domain.usecase

import com.example.baro.feature.party.domain.model.CreateParty
import com.example.baro.feature.party.domain.repository.PartyRepository

class CreatePartyUseCase(
    private val repository: PartyRepository
) {
    suspend operator fun invoke(request: CreateParty) =
        repository.createParty(request)
}
