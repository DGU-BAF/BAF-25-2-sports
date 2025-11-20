# app/modules/bot/router.py
from __future__ import annotations

from typing import Dict, List

from fastapi import APIRouter, HTTPException

from app.modules.bot.schemas import (
    ChatMessage,
    ChatRoom,
    ChatRoomSummary,
    SenderType,
    SendMessageRequest,
    SendMessageResponse,
)

router = APIRouter(prefix="/bot", tags=["bot"])

# ---------------------------------------------------------
# 인메모리 목업 데이터 (서버 껐다 켜면 초기화됨)
# ---------------------------------------------------------
_rooms: Dict[str, ChatRoom] = {}


def _ensure_seed_data() -> None:
    """서버 시작 후 최초 요청 시 기본 채팅방 몇 개 만들어 둔다."""
    global _rooms
    if _rooms:
        return

    # 방 1: 운동 루틴 상담
    m1 = ChatMessage.create(
        text="안녕하세요! 오늘은 어떤 운동을 하고 싶으세요?",
        sender=SenderType.BOT,
    )
    room1 = ChatRoom.create(
        title="오늘 운동 뭐하지?",
        initial_messages=[m1],
    )

    # 방 2: 체육관 추천
    m2 = ChatMessage.create(
        text="집 근처 추천 헬스장 알려드릴까요?",
        sender=SenderType.BOT,
    )
    room2 = ChatRoom.create(
        title="헬스장 추천 받기",
        initial_messages=[m2],
    )

    _rooms[room1.id] = room1
    _rooms[room2.id] = room2


def _room_to_summary(room: ChatRoom) -> ChatRoomSummary:
    return ChatRoomSummary(
        id=room.id,
        title=room.title,
        lastMessage=room.lastMessage,
        createdAt=room.createdAt,
    )


# ---------------------------------------------------------
# 1) 채팅방 목록
# ---------------------------------------------------------
@router.get("/rooms", response_model=List[ChatRoomSummary])
async def list_rooms() -> List[ChatRoomSummary]:
    _ensure_seed_data()
    return [_room_to_summary(r) for r in _rooms.values()]


# ---------------------------------------------------------
# 2) 특정 채팅방 전체 정보 (메시지 포함)
# ---------------------------------------------------------
@router.get("/rooms/{room_id}", response_model=ChatRoom)
async def get_room(room_id: str) -> ChatRoom:
    _ensure_seed_data()
    room = _rooms.get(room_id)
    if not room:
        raise HTTPException(status_code=404, detail="Chat room not found")
    return room


# ---------------------------------------------------------
# 3) 특정 채팅방 메시지 목록만
# ---------------------------------------------------------
@router.get("/rooms/{room_id}/messages", response_model=List[ChatMessage])
async def list_messages(room_id: str) -> List[ChatMessage]:
    _ensure_seed_data()
    room = _rooms.get(room_id)
    if not room:
        raise HTTPException(status_code=404, detail="Chat room not found")
    return room.messages


# ---------------------------------------------------------
# 4) 메시지 전송 (USER → BOT 목업 답변까지)
# ---------------------------------------------------------
@router.post("/rooms/{room_id}/messages", response_model=SendMessageResponse)
async def send_message(room_id: str, req: SendMessageRequest) -> SendMessageResponse:
    _ensure_seed_data()
    room = _rooms.get(room_id)
    if not room:
        raise HTTPException(status_code=404, detail="Chat room not found")

    # 사용자 메시지
    user_msg = ChatMessage.create(
        text=req.text,
        sender=SenderType.USER,
    )
    room.messages.append(user_msg)

    # 간단한 목업 봇 응답 (LLM 대신 고정 규칙)
    reply_text = f"사용자님이 이렇게 말했어요: \"{req.text}\". 곧 운동 추천 기능이 연결될 예정입니다!"
    bot_msg = ChatMessage.create(
        text=reply_text,
        sender=SenderType.BOT,
    )
    room.messages.append(bot_msg)

    # lastMessage 갱신
    room.lastMessage = bot_msg.text

    return SendMessageResponse(messages=[user_msg, bot_msg])
