package com.example.baro.feature.bot.domain.usecase

import com.example.baro.feature.bot.data.BotRepository
import com.example.baro.feature.bot.domain.model.ChatRoom

class SendUserMessageUseCase(
    private val repository: BotRepository
) {
    /**
     * roomId 방에 사용자의 messageText 를 보내고,
     * Bot 응답까지 반영된 최신 ChatRoom 을 반환.
     */
    suspend operator fun invoke(
        roomId: String,
        messageText: String
    ): ChatRoom {
        return repository.sendUserMessage(
            roomId = roomId,
            messageText = messageText
        )
    }
}
