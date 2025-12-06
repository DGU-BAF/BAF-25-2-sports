package com.example.baro.feature.bot

import com.example.baro.core.network.NetworkModule
import com.example.baro.feature.auth.data.local.SessionManager
import com.example.baro.feature.bot.data.BotRepository
import com.example.baro.feature.bot.data.BotRepositoryImpl
import com.example.baro.feature.bot.data.local.BotLocalDataSource
import com.example.baro.feature.bot.data.local.InMemoryBotLocalDataSource
import com.example.baro.feature.bot.data.remote.BotApiService
import com.example.baro.feature.bot.data.remote.BotRemoteDataSource
import com.example.baro.feature.bot.data.remote.RealBotRemoteDataSource
import com.example.baro.feature.bot.domain.usecase.*

object BotServiceLocator {

    // 인증된 Retrofit으로 초기화해야 하므로 lateinit 사용
    private lateinit var apiService: BotApiService

    /**
     * 앱 시작 시 GlobalApplication.onCreate()에서 호출됨
     * SessionManager를 넣어서 토큰 포함된 Retrofit 생성
     */
    fun init(sessionManager: SessionManager) {
        val retrofit = NetworkModule.createAuthorizedRetrofit(sessionManager)
        apiService = retrofit.create(BotApiService::class.java)
    }

    // Local DataSource
    private val localDataSource: BotLocalDataSource by lazy {
        InMemoryBotLocalDataSource()
    }

    // Remote DataSource (apiService가 init 후 생성됨)
    private val remoteDataSource: BotRemoteDataSource by lazy {
        RealBotRemoteDataSource(apiService)
    }

    // Repository
    val botRepository: BotRepository by lazy {
        BotRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource
        )
    }

    // --- UseCases ---
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
