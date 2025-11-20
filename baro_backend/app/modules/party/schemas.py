from pydantic import BaseModel
from typing import List, Optional
from enum import Enum
from datetime import datetime


class PartyStatus(str, Enum):
    SCHEDULED = "scheduled"
    COMPLETED = "completed"
    CANCELLED = "cancelled"


class PartyMemberRole(str, Enum):
    HOST = "HOST"
    MEMBER = "MEMBER"


class PartyMemberStatus(str, Enum):
    JOINED = "JOINED"
    LEFT = "LEFT"
    KICKED = "KICKED"


class PartyMember(BaseModel):
    partyId: str
    userId: str
    nickname: str
    role: PartyMemberRole
    status: PartyMemberStatus
    joinedAt: Optional[str] = None
    sportsmanship: Optional[int] = None


class CreateParty(BaseModel):
    title: str
    sport: str
    place: str
    description: str
    date: str
    startTime: str
    endTime: str
    capacity: int


class PartySummary(BaseModel):
    partyId: str
    title: str
    sport: str
    place: str
    description: str
    date: str
    startTime: str
    endTime: str
    capacity: int
    current: int
    hostId: str
    status: PartyStatus
    isJoined: bool
    createdAt: str


class PartyDetail(BaseModel):
    partyId: str
    title: str
    sport: str
    place: str
    description: str
    date: str
    startTime: str
    endTime: str
    capacity: int
    current: int
    hostId: str
    status: PartyStatus
    isJoined: bool
    createdAt: str
    placeLat: Optional[float] = None
    placeLng: Optional[float] = None
    members: List[PartyMember]
