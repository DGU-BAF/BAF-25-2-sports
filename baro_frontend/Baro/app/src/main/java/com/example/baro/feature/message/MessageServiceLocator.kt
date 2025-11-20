package com.example.baro.feature.message

import com.example.baro.core.network.NetworkModule
import com.example.baro.feature.message.data.api.MessageApi
import com.example.baro.feature.message.data.repository.MessageRepositoryImpl
import com.example.baro.feature.message.data.source.MessageRemoteSource
import com.example.baro.feature.message.data.source.MessageRemoteSourceImpl
import com.example.baro.feature.message.domain.repository.MessageRepository
import com.example.baro.feature.message.domain.usecase.GetMessageRoomsUseCase
import com.example.baro.feature.message.domain.usecase.GetMessagesUseCase
import com.example.baro.feature.message.domain.usecase.SendMessageUseCase
import com.example.baro.feature.message.domain.usecase.ObserveMessagesUseCase

object MessageServiceLocator {

    // Retrofit API
    val messageApi: MessageApi by lazy {
        NetworkModule.createApi(MessageApi::class.java)
    }

    // Remote source
    val remoteSource: MessageRemoteSource by lazy {
        MessageRemoteSourceImpl(messageApi)
    }

    // Repository
    val messageRepository: MessageRepository by lazy {
        MessageRepositoryImpl(
            remoteSource = remoteSource,
            currentUserIdProvider = { "TEMP_USER_ID" }   // TODO: 실제 유저 ID로 교체
        )
    }

    // UseCases
    val getMessageRoomsUseCase by lazy { GetMessageRoomsUseCase(messageRepository) }
    val getMessagesUseCase by lazy { GetMessagesUseCase(messageRepository) }
    val sendMessageUseCase by lazy { SendMessageUseCase(messageRepository) }
    val observeMessagesUseCase by lazy { ObserveMessagesUseCase(messageRepository) }
}
