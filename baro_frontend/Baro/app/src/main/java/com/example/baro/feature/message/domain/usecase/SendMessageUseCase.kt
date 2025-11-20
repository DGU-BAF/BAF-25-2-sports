// com/example/baro/feature/message/domain/usecase/SendMessageUseCase.kt
package com.example.baro.feature.message.domain.usecase

import com.example.baro.feature.message.domain.repository.MessageRepository

class SendMessageUseCase(
    private val messageRepository: MessageRepository
) {

    suspend operator fun invoke(
        roomId: String,
        content: String
    ) {
        messageRepository.sendMessage(
            roomId = roomId,
            content = content
        )
    }
}
