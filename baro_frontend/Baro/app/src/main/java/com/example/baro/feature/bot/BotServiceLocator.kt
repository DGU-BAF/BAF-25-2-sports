package com.example.baro.feature.bot

import com.example.baro.core.network.NetworkModule
import com.example.baro.feature.bot.data.BotRepository
import com.example.baro.feature.bot.data.BotRepositoryImpl
import com.example.baro.feature.bot.data.local.BotLocalDataSource
import com.example.baro.feature.bot.data.local.InMemoryBotLocalDataSource
import com.example.baro.feature.bot.data.remote.BotApiService
import com.example.baro.feature.bot.data.remote.BotRemoteDataSource
import com.example.baro.feature.bot.data.remote.RealBotRemoteDataSource
import com.example.baro.feature.bot.domain.usecase.CreateChatRoomUseCase
import com.example.baro.feature.bot.domain.usecase.GetChatRoomUseCase
import com.example.baro.feature.bot.domain.usecase.LoadChatRoomsUseCase
import com.example.baro.feature.bot.domain.usecase.SendUserMessageUseCase
import com.example.baro.feature.bot.domain.usecase.UpdateRoomTitleUseCase

object BotServiceLocator {

    // --- ApiService (Gson 기반) ---
    private val apiService: BotApiService by lazy {
        NetworkModule.createGsonApi(BotApiService::class.java)
    }

    // --- DataSource ---
    private val localDataSource: BotLocalDataSource by lazy {
        InMemoryBotLocalDataSource()
    }

    private val remoteDataSource: BotRemoteDataSource by lazy {
        RealBotRemoteDataSource(apiService)
    }

    // --- Repository ---
    val botRepository: BotRepository by lazy {
        BotRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource
        )
    }

    // --- UseCases (원하면 ViewModel에서 직접 써도 됨) ---
    val loadChatRoomsUseCase: LoadChatRoomsUseCase by lazy {
        LoadChatRoomsUseCase(botRepository)
    }

    val getChatRoomUseCase: GetChatRoomUseCase by lazy {
        GetChatRoomUseCase(botRepository)
    }

    val createChatRoomUseCase: CreateChatRoomUseCase by lazy {
        CreateChatRoomUseCase(botRepository)
    }

    val sendUserMessageUseCase: SendUserMessageUseCase by lazy {
        SendUserMessageUseCase(botRepository)
    }

    val updateRoomTitleUseCase: UpdateRoomTitleUseCase by lazy {
        UpdateRoomTitleUseCase(botRepository)
    }
}
