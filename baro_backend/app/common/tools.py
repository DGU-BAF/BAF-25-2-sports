# app/tools.py
from typing import Optional, List, Dict, Any
from langchain_core.tools import tool

from ..core.db import get_nearby_facilities


@tool
def nearby_sports_facilities(
    user_lat: float,
    user_lon: float,
    sports_type: Optional[str] = None,
    inout: Optional[str] = None,
    limit: int = 5,
) -> List[Dict[str, Any]]:
    """
    송파구 생활체육 시설 중 사용자 위치에서 가까운 순으로 운동 시설을 찾습니다.
    - user_lat, user_lon: 사용자의 위도/경도 (decimal degrees)
    - sports_type: 원하는 운동 타입 (예: '축구', '농구', '헬스'); 없으면 전체
    - inout: '실내' 또는 '실외'; 없으면 전체
    - limit: 최대 추천 개수
    """
    return get_nearby_facilities(
        user_lat=user_lat,
        user_lon=user_lon,
        sports_type=sports_type,
        inout=inout,
        limit=limit,
    )