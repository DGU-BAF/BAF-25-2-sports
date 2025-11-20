package com.example.baro.feature.bot.domain.usecase

import com.example.baro.feature.bot.data.BotRepository
import com.example.baro.feature.bot.domain.model.ChatRoom

class GetChatRoomUseCase(
    private val repository: BotRepository
) {
    suspend operator fun invoke(roomId: String): ChatRoom? {
        return repository.getChatRoom(roomId)
    }
}
