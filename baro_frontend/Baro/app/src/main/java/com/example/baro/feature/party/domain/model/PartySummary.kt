package com.example.baro.feature.party.domain.model

data class PartySummary(
    val partyId: String,      // party_id
    val title: String,        // title
    val sport: String,        // sport
    val place: String,        // place
    val description: String,  // description
    val date: String,         // date
    val startTime: String,    // start_time
    val endTime: String,      // end_time
    val capacity: Int,        // capacity
    val current: Int,         // current
    val hostId: String,       // host_id
    val status: PartyStatus,  // status
    val isJoined: Boolean,    // isJoined (현재 유저 기준)
    val createdAt: String,    // created_at
) {
    val isFull: Boolean
        get() = current >= capacity

    val isScheduled: Boolean
        get() = status == PartyStatus.SCHEDULED
}
