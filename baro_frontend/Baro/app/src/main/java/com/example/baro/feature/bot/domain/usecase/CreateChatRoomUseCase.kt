package com.example.baro.feature.bot.domain.usecase

import com.example.baro.feature.bot.data.BotRepository
import com.example.baro.feature.bot.domain.model.ChatRoom

class CreateChatRoomUseCase(
    private val repository: BotRepository
) {
    /**
     * initialMessage: 처음에 바로 질문을 넣고 방을 만들고 싶을 때 사용 (없으면 null)
     */
    suspend operator fun invoke(
        initialMessage: String? = null
    ): ChatRoom {
        return repository.createChatRoom(initialMessage)
    }
}
