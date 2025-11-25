# app/schemas.py
from pydantic import BaseModel
from typing import Optional


class ChatRequest(BaseModel):
    message: str
    user_lat: Optional[float] = None
    user_lon: Optional[float] = None
    weight_kg: Optional[float] = None
    height_cm: Optional[float] = None
    goal_weight_kg: Optional[float] = None
    weekly_goal_kg: Optional[float] = None
    # 앞으로 확장하고 싶으면 여기 sports_type, inout 넣어서 UI에서 받도록 해도 됨.


class ChatResponse(BaseModel):
    answer: str