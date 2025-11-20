package com.example.baro.feature.party.data.source

import com.example.baro.feature.party.data.model.CreatePartyRequestDto
import com.example.baro.feature.party.data.model.PartyDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PartyApi {

    // FastAPI: GET /party
    @GET("party")
    suspend fun getPartyList(): List<PartyDto>

    // FastAPI: GET /party/{party_id}
    @GET("party/{partyId}")
    suspend fun getPartyDetail(
        @Path("partyId") partyId: String
    ): PartyDto

    // FastAPI: POST /party
    @POST("party")
    suspend fun createParty(
        @Body request: CreatePartyRequestDto
    ): PartyDto

    // FastAPI: POST /party/{party_id}/join
    @POST("party/{partyId}/join")
    suspend fun joinParty(
        @Path("partyId") partyId: String
    ): PartyDto

    // FastAPI: POST /party/{party_id}/leave
    @POST("party/{partyId}/leave")
    suspend fun leaveParty(
        @Path("partyId") partyId: String
    ): PartyDto
}
