// com/example/baro/feature/message/domain/usecase/GetMessageRoomsUseCase.kt
package com.example.baro.feature.message.domain.usecase

import com.example.baro.feature.message.domain.model.MessageRoomSummary
import com.example.baro.feature.message.domain.repository.MessageRepository

class GetMessageRoomsUseCase(
    private val messageRepository: MessageRepository
) {

    suspend operator fun invoke(): List<MessageRoomSummary> {
        return messageRepository.getMessageRooms()
    }
}
