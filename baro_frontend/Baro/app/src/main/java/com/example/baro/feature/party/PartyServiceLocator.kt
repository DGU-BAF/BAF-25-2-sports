package com.example.baro.feature.party

import com.example.baro.feature.party.data.repository.PartyRepositoryImpl
import com.example.baro.feature.party.data.source.PartyApi
import com.example.baro.feature.party.data.source.PartyRemoteDataSource
import com.example.baro.feature.party.data.source.PartyRemoteDataSourceImpl
import com.example.baro.feature.party.domain.repository.PartyRepository
import com.example.baro.feature.party.domain.usecase.CreatePartyUseCase
import com.example.baro.feature.party.domain.usecase.GetPartyDetailUseCase
import com.example.baro.feature.party.domain.usecase.GetPartyListUseCase
import com.example.baro.feature.party.domain.usecase.GetUpcomingJoinedPartiesUseCase
import com.example.baro.feature.party.domain.usecase.JoinPartyUseCase
import com.example.baro.feature.party.domain.usecase.LeavePartyUseCase
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PartyServiceLocator {

    // FastAPI 서버 주소 (BotServiceLocator 와 동일하게 맞추기)
    private const val BASE_URL = "http://192.168.45.157:8000/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .build()
    }

    // ★ 여기 Retrofit은 무조건 GsonConverterFactory 사용 ★
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val partyApi: PartyApi by lazy {
        retrofit.create(PartyApi::class.java)
    }

    private val remoteDataSource: PartyRemoteDataSource by lazy {
        PartyRemoteDataSourceImpl(partyApi)
    }

    private val partyRepository: PartyRepository by lazy {
        PartyRepositoryImpl(remoteDataSource)
    }

    // --- UseCases ---

    val getPartyListUseCase: GetPartyListUseCase by lazy {
        GetPartyListUseCase(partyRepository)
    }

    val getUpcomingJoinedPartiesUseCase: GetUpcomingJoinedPartiesUseCase by lazy {
        GetUpcomingJoinedPartiesUseCase(partyRepository)
    }

    val getPartyDetailUseCase: GetPartyDetailUseCase by lazy {
        GetPartyDetailUseCase(partyRepository)
    }

    val createPartyUseCase: CreatePartyUseCase by lazy {
        CreatePartyUseCase(partyRepository)
    }

    val joinPartyUseCase: JoinPartyUseCase by lazy {
        JoinPartyUseCase(partyRepository)
    }

    val leavePartyUseCase: LeavePartyUseCase by lazy {
        LeavePartyUseCase(partyRepository)
    }
}
