package com.example.baro.feature.bot.domain.usecase

import com.example.baro.feature.bot.data.BotRepository
import com.example.baro.feature.bot.domain.model.ChatRoomSummary

class LoadChatRoomsUseCase(
    private val repository: BotRepository
) {
    suspend operator fun invoke(): List<ChatRoomSummary> {
        return repository.getChatRooms()
    }
}
