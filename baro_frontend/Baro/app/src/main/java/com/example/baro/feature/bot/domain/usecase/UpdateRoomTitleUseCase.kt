package com.example.baro.feature.bot.domain.usecase

import com.example.baro.feature.bot.data.BotRepository

class UpdateRoomTitleUseCase(
    private val repository: BotRepository
) {
    suspend operator fun invoke(
        roomId: String,
        newTitle: String
    ) {
        repository.updateRoomTitle(
            roomId = roomId,
            newTitle = newTitle
        )
    }
}
