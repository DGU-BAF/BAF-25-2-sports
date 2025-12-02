# app/modules/bot/schemas.py

from __future__ import annotations

from pydantic import BaseModel
from typing import List, Literal, Optional

# -----------------------------
# 기존 챗봇 엔진용 스키마
# -----------------------------

class ChatRequest(BaseModel):
    """
    process_bot_message 에서 req.xxx 로 접근하는 필드를 모두 정의한 모델
    """

    # (필수) 사용자가 실제로 보낸 메시지
    message: str
    
    # [수정] 대화 맥락을 유지하기 위한 고유 ID (프론트엔드에서 생성한 채팅방 ID 또는 UUID)
    # Optional로 처리하여 기존 코드와의 호환성을 유지하되, 
    # 기억 기능을 사용하려면 프론트엔드에서 반드시 값을 보내야 함
    thread_id: str | None = None

    # (1) 기본 정보
    nickname: str | None = None
    gender: str | None = None
    birth_date: str | None = None   # "YYYY-MM-DD" 형식

    # (2) 신체 정보
    height: float | None = None        # cm
    weight: float | None = None        # kg
    muscle_mass: float | None = None   # kg

    # (3) 운동 성향
    skill_level: str | None = None
    favorite_sports: List[str] | None = None

    # (4) 위치 정보
    latitude: float | None = None
    longitude: float | None = None

    # (5) 기타 컨텍스트
    user_id: str | None = None
    room_id: str | None = None

class ChatResponse(BaseModel):
    """
    기존 /chatbot/message 에서 쓰던 응답 모델
    """
    answer: str


# -----------------------------
# 안드로이드용 DTO 대응 스키마
# -----------------------------

class ChatRoomSummary(BaseModel):
    """
    안드로이드 ChatRoomSummaryDto 에 대응
    """
    id: str
    title: str
    lastMessage: str
    createdAt: int  # epoch millis


class ChatMessage(BaseModel):
    """
    안드로이드 ChatMessageDto 에 대응
    """
    id: str
    text: str
    sender: Literal["USER", "BOT"]
    timestamp: int  # epoch millis


class BotRequest(BaseModel):
    """
    안드로이드 BotRequestDto 에 대응
    """
    text: str
    
    # [참고] 만약 안드로이드 BotRequestDto 쪽에서도 thread_id를 보낸다면 여기도 추가 필요
    thread_id: str | None = None 


class BotResponse(BaseModel):
    """
    안드로이드 BotResponseDto 에 대응
    """
    messages: List[ChatMessage]