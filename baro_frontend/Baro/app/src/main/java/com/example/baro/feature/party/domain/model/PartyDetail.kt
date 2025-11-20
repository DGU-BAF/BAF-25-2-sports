package com.example.baro.feature.party.domain.model

data class PartyDetail(
    val partyId: String,          // party_id
    val title: String,            // title
    val sport: String,            // sport
    val place: String,            // place
    val description: String,      // description
    val date: String,             // date
    val startTime: String,        // start_time
    val endTime: String,          // end_time
    val capacity: Int,            // capacity
    val current: Int,             // current
    val hostId: String,           // host_id
    val status: PartyStatus,      // status
    val isJoined: Boolean,        // isJoined
    val createdAt: String,        // created_at
    val placeLat: Double?,        // place_lat
    val placeLng: Double?,        // place_lng
    val members: List<PartyMember>// members (member 테이블 기반)
) {
    val isFull: Boolean
        get() = current >= capacity

    val isScheduled: Boolean
        get() = status == PartyStatus.SCHEDULED

    val isHost: Boolean
        get() = members.any { it.role == PartyMemberRole.HOST && it.userId == hostId }
}
