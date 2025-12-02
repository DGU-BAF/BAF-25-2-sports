# app/modules/bot/router.py

from __future__ import annotations

import logging
import time
import uuid
from typing import Dict, List

from fastapi import APIRouter, HTTPException

# 안드로이드용 스키마
from app.modules.bot.schemas import (
    BotRequest,
    BotResponse,
    ChatMessage,
    ChatRoomSummary,
    ChatRequest,
    ChatResponse,
)

# 기존 챗봇 엔진용 스키마/서비스
from app.modules.bot.schemas import ChatRequest as AgentChatRequest
from app.modules.bot.service import process_bot_message

router = APIRouter(prefix="/bot", tags=["Bot"])
logger = logging.getLogger(__name__)

_rooms_messages: Dict[str, List[ChatMessage]] = {}
_rooms_meta: Dict[str, ChatRoomSummary] = {}


def _now_millis() -> int:
    return int(time.time() * 1000)


def _ensure_room_exists(room_id: str) -> None:
    if room_id not in _rooms_messages:
        raise HTTPException(status_code=404, detail="Room not found")


def _update_room_summary(room_id: str, last_message_text: str) -> None:
    now = _now_millis()
    if room_id in _rooms_meta:
        summary = _rooms_meta[room_id]
        _rooms_meta[room_id] = ChatRoomSummary(
            id=summary.id,
            title=summary.title,
            lastMessage=last_message_text,
            createdAt=summary.createdAt,
        )
    else:
        _rooms_meta[room_id] = ChatRoomSummary(
            id=room_id,
            title=f"채팅방 {room_id[:8]}",
            lastMessage=last_message_text,
            createdAt=now,
        )


@router.get("/rooms", response_model=List[ChatRoomSummary])
def get_chat_rooms() -> List[ChatRoomSummary]:
    return list(_rooms_meta.values())


@router.get("/rooms/{room_id}/messages", response_model=List[ChatMessage])
def get_messages(room_id: str) -> List[ChatMessage]:
    if room_id not in _rooms_messages:
        # 아직 메시지가 한 번도 없는 방이라면 빈 리스트 반환
        _rooms_messages[room_id] = []
    return _rooms_messages[room_id]


@router.post("/rooms/{room_id}/messages", response_model=BotResponse)
def send_message(room_id: str, req: BotRequest) -> BotResponse:
    logger.info("send_message called: room_id=%s, text=%s", room_id, req.text)

    if room_id not in _rooms_messages:
        _rooms_messages[room_id] = []

    # 1) 유저 메시지 저장
    now = _now_millis()
    user_msg = ChatMessage(
        id=str(uuid.uuid4()),
        text=req.text,
        sender="USER",
        timestamp=now,
    )
    _rooms_messages[room_id].append(user_msg)

    # 2) 기존 챗봇 엔진 호출할 때 thread_id = room_id 로 사용
    agent_req = AgentChatRequest(
        message=req.text,
        thread_id=room_id,   # ← 여기 중요
        room_id=room_id,     # (원하면 같이 세팅)
        # user_id 등 나중에 필요하면 추가
    )
    agent_answer = process_bot_message(agent_req)

    # 3) 봇 메시지 저장
    bot_msg = ChatMessage(
        id=str(uuid.uuid4()),
        text=agent_answer,
        sender="BOT",
        timestamp=_now_millis(),
    )
    _rooms_messages[room_id].append(bot_msg)

    _update_room_summary(room_id, last_message_text=bot_msg.text)

    return BotResponse(messages=[bot_msg])