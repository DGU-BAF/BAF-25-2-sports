package com.example.baro.feature.party.domain.model

data class CreateParty(
    val title: String,
    val sport: String,
    val place: String,
    val description: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val capacity: Int,
)
