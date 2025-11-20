# app/modules/bot/schemas.py
from __future__ import annotations

from enum import Enum
from typing import List
from uuid import uuid4
import time

from pydantic import BaseModel


class SenderType(str, Enum):
    USER = "USER"
    BOT = "BOT"


class ChatMessage(BaseModel):
    id: str
    text: str
    sender: SenderType
    timestamp: int  # millis since epoch (Android Long)

    @classmethod
    def create(cls, text: str, sender: SenderType) -> "ChatMessage":
        return cls(
            id=str(uuid4()),
            text=text,
            sender=sender,
            timestamp=int(time.time() * 1000),
        )


class ChatRoom(BaseModel):
    id: str
    title: str
    lastMessage: str
    messages: List[ChatMessage]
    createdAt: int  # millis

    @classmethod
    def create(cls, title: str, initial_messages: List[ChatMessage]) -> "ChatRoom":
        created_at = int(time.time() * 1000)
        last_msg = initial_messages[-1].text if initial_messages else ""
        return cls(
            id=str(uuid4()),
            title=title,
            lastMessage=last_msg,
            messages=initial_messages,
            createdAt=created_at,
        )


class ChatRoomSummary(BaseModel):
    id: str
    title: str
    lastMessage: str
    createdAt: int


class SendMessageRequest(BaseModel):
    text: str


class SendMessageResponse(BaseModel):
    """
    - 방에 추가된 사용자 메시지 + 봇 응답을 같이 내려주는 형태
    - 안드로이드에서는 messages[0], messages[1] 순서대로 붙이면 됨
    """
    messages: List[ChatMessage]
