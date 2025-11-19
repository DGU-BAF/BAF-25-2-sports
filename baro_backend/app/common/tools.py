# app/tools.py
from typing import Optional, List, Dict, Any
from langchain_core.tools import tool
from .weather import get_simple_weather

from .db import get_profiled_facilities


@tool
def profile_based_sports_facilities(
    user_lat: float,
    user_lon: float,
    preferred_sports: Optional[List[str]] = None,
    age: Optional[int] = None,
    gender: Optional[str] = None,              # '남' / '여'
    preferred_intensity: Optional[str] = None, # '저' / '중' / '고'
    limit: int = 5,
) -> List[Dict[str, Any]]:
    """
    사용자 프로필(선호 종목, 나이, 성별, 선호 강도)을 반영한 운동 시설 추천.
    거리 > 선호종목 > 나이 > 성별 > 강도 > 연령별 선호스포츠 순으로 가중치를 둔다.
    """
    return get_profiled_facilities(
        user_lat=user_lat,
        user_lon=user_lon,
        preferred_sports=preferred_sports,
        age=age,
        gender=gender,
        preferred_intensity=preferred_intensity,
        limit=limit,
    )

@tool
def current_weather(
    user_lat: float,
    user_lon: float,
) -> str:
    """
    사용자 위치 기준 현재 날씨를 알려줍니다.
    - 현재 기온(°C)
    - 하늘 상태: 맑음 / 비 / 눈 / 비 또는 눈 / 알 수 없음
    """
    info = get_simple_weather(user_lat, user_lon)
    if info is None:
        return "지금은 기상청 날씨 정보를 가져오지 못했어요. 잠시 후 다시 시도해 주세요."

    temp = info["temp_c"]
    cond = info["condition"]

    if temp is None:
        return f"현재 하늘 상태는 {cond}입니다."

    return f"현재 기온은 약 {temp:.1f}도이고, 하늘 상태는 {cond}입니다."