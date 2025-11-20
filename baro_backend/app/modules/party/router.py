from fastapi import APIRouter, HTTPException
from uuid import uuid4
from datetime import datetime

from .schemas import (
    CreateParty,
    PartyDetail,
    PartySummary,
    PartyStatus,
    PartyMember,
    PartyMemberRole,
    PartyMemberStatus,
)

router = APIRouter(prefix="/party", tags=["party"])

# -----------------------------
# 메모리 기반 목업 저장소
# -----------------------------
MOCK_PARTIES: dict[str, PartyDetail] = {}


def _seed_mock_parties() -> None:
    """
    서버 시작 시 한 번 호출해서 기본 목업 파티 몇 개 만들어 둔다.
    """
    if MOCK_PARTIES:  # 이미 있으면 다시 안 만듦
        return

    now = datetime.utcnow().isoformat()

    # 1번 파티
    party_id_1 = str(uuid4())
    host_member_1 = PartyMember(
        partyId=party_id_1,
        userId="user_host_1",
        nickname="방장1",
        role=PartyMemberRole.HOST,
        status=PartyMemberStatus.JOINED,
        joinedAt=now,
        sportsmanship=52,
    )

    party1 = PartyDetail(
        partyId=party_id_1,
        title="탄천 러닝 같이 하실 분",
        sport="running",
        place="탄천 운동장",
        description="저녁에 5km 가볍게 뜁니다.",
        date="2025-11-21",
        startTime="19:00",
        endTime="20:30",
        capacity=5,
        current=1,
        hostId="user_host_1",
        status=PartyStatus.SCHEDULED,
        isJoined=True,  # 가상의 현재 사용자도 참여했다고 가정
        createdAt=now,
        placeLat=37.505,
        placeLng=127.12,
        members=[host_member_1],
    )

    # 2번 파티
    party_id_2 = str(uuid4())
    host_member_2 = PartyMember(
        partyId=party_id_2,
        userId="user_host_2",
        nickname="방장2",
        role=PartyMemberRole.HOST,
        status=PartyMemberStatus.JOINED,
        joinedAt=now,
        sportsmanship=48,
    )

    member_2 = PartyMember(
        partyId=party_id_2,
        userId="user_member_1",
        nickname="참가자1",
        role=PartyMemberRole.MEMBER,
        status=PartyMemberStatus.JOINED,
        joinedAt=now,
        sportsmanship=45,
    )

    party2 = PartyDetail(
        partyId=party_id_2,
        title="잠실 배드민턴 원데이",
        sport="badminton",
        place="잠실 종합운동장 실내체육관",
        description="초보도 환영합니다. 라켓 대여 가능.",
        date="2025-11-22",
        startTime="14:00",
        endTime="16:00",
        capacity=8,
        current=2,
        hostId="user_host_2",
        status=PartyStatus.SCHEDULED,
        isJoined=False,  # 현재 사용자는 아직 안 들어간 걸로
        createdAt=now,
        placeLat=37.515,
        placeLng=127.07,
        members=[host_member_2, member_2],
    )

    MOCK_PARTIES[party_id_1] = party1
    MOCK_PARTIES[party_id_2] = party2


# 서버 로드 시 한 번 실행
_seed_mock_parties()


# -----------------------------
# 파티 생성
# -----------------------------
@router.post("", response_model=PartyDetail)
def create_party(data: CreateParty):
    party_id = str(uuid4())
    now = datetime.utcnow().isoformat()

    host_member = PartyMember(
        partyId=party_id,
        userId="mock_user_host",
        nickname="방장",
        role=PartyMemberRole.HOST,
        status=PartyMemberStatus.JOINED,
        joinedAt=now,
        sportsmanship=50,
    )

    party = PartyDetail(
        partyId=party_id,
        title=data.title,
        sport=data.sport,
        place=data.place,
        description=data.description,
        date=data.date,
        startTime=data.startTime,
        endTime=data.endTime,
        capacity=data.capacity,
        current=1,
        hostId="mock_user_host",
        status=PartyStatus.SCHEDULED,
        isJoined=True,
        createdAt=now,
        placeLat=37.55,
        placeLng=127.00,
        members=[host_member],
    )

    MOCK_PARTIES[party_id] = party
    return party


# -----------------------------
# 파티 목록 조회
# -----------------------------
@router.get("", response_model=list[PartySummary])
def get_party_list():
    summaries: list[PartySummary] = []

    for party in MOCK_PARTIES.values():
        summaries.append(
            PartySummary(
                partyId=party.partyId,
                title=party.title,
                sport=party.sport,
                place=party.place,
                description=party.description,
                date=party.date,
                startTime=party.startTime,
                endTime=party.endTime,
                capacity=party.capacity,
                current=party.current,
                hostId=party.hostId,
                status=party.status,
                isJoined=party.isJoined,
                createdAt=party.createdAt,
            )
        )

    return summaries


# -----------------------------
# 파티 상세 조회
# -----------------------------
@router.get("/{party_id}", response_model=PartyDetail)
def get_party_detail(party_id: str):
    if party_id not in MOCK_PARTIES:
        raise HTTPException(status_code=404, detail="Party not found")
    return MOCK_PARTIES[party_id]


# -----------------------------
# 파티 참가
# -----------------------------
@router.post("/{party_id}/join", response_model=PartyDetail)
def join_party(party_id: str):
    if party_id not in MOCK_PARTIES:
        raise HTTPException(status_code=404, detail="Party not found")

    party = MOCK_PARTIES[party_id]

    # 이미 참여 상태라면 그대로 반환
    if party.isJoined:
        return party

    new_member = PartyMember(
        partyId=party_id,
        userId="mock_user_1",
        nickname="참가자 A",
        role=PartyMemberRole.MEMBER,
        status=PartyMemberStatus.JOINED,
        joinedAt=datetime.utcnow().isoformat(),
        sportsmanship=45,
    )

    party.members.append(new_member)
    party.current += 1
    party.isJoined = True

    return party


# -----------------------------
# 파티 탈퇴
# -----------------------------
@router.post("/{party_id}/leave", response_model=PartyDetail)
def leave_party(party_id: str):
    if party_id not in MOCK_PARTIES:
        raise HTTPException(status_code=404, detail="Party not found")

    party = MOCK_PARTIES[party_id]

    before = len(party.members)
    party.members = [m for m in party.members if m.userId != "mock_user_1"]
    after = len(party.members)

    if before != after:
        party.current -= 1

    party.isJoined = False

    return party
