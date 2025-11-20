// com/example/baro/feature/message/domain/usecase/ObserveMessagesUseCase.kt
package com.example.baro.feature.message.domain.usecase

import com.example.baro.feature.message.domain.model.Message
import com.example.baro.feature.message.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

class ObserveMessagesUseCase(
    private val messageRepository: MessageRepository
) {

    operator fun invoke(roomId: String): Flow<Message> {
        return messageRepository.observeMessages(roomId)
    }
}
