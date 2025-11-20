// com/example/baro/feature/message/domain/usecase/GetMessagesUseCase.kt
package com.example.baro.feature.message.domain.usecase

import com.example.baro.feature.message.domain.model.Message
import com.example.baro.feature.message.domain.repository.MessageRepository

class GetMessagesUseCase(
    private val messageRepository: MessageRepository
) {

    suspend operator fun invoke(roomId: String): List<Message> {
        return messageRepository.getMessages(roomId)
    }
}
