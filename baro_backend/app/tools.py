# app/tools.py
from typing import Optional, List, Dict, Any
from langchain_core.tools import tool
from .weather import get_simple_weather

from .db import get_profiled_facilities, get_nearby_parties


def _bmi_category(bmi: float) -> str:
    if bmi < 18.5:
        return "저체중"
    if bmi < 23:
        return "정상"
    if bmi < 25:
        return "과체중"
    return "비만"


def _activity_multiplier(level: str) -> float:
    """
    체중(kg)당 일일 유지 칼로리 계수를 반환.
    좌식 28, 중간 33, 활동적 38 kcal/kg/day를 기본값으로 사용.
    """
    base = {
        "낮음": 28,
        "중간": 33,
        "높음": 38,
    }
    return base.get(level, base["중간"])


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


@tool
def weight_management_plan(
    height_cm: float,
    weight_kg: float,
    goal_weight_kg: Optional[float] = None,
    weekly_goal_kg: float = 0.5,
    activity_level: str = "중간",  # 낮음 / 중간 / 높음
) -> Dict[str, Any]:
    """
    사용자의 키/체중/목표를 받아 체중 관리 가이드를 계산합니다.
    - BMI와 카테고리(저체중/정상/과체중/비만)
    - 활동량을 반영한 유지 칼로리(rough estimate)
    - 목표 체중을 위한 일일 섭취 가이드와 예상 기간
    의료 상담을 대체하지 않음을 항상 강조하세요.
    """

    height_m = height_cm / 100
    if height_m <= 0:
        raise ValueError("height_cm은 0보다 커야 합니다.")
    if weight_kg <= 0:
        raise ValueError("weight_kg는 0보다 커야 합니다.")

    bmi = weight_kg / (height_m**2)
    bmi_cat = _bmi_category(bmi)

    kcal_per_kg = _activity_multiplier(activity_level)
    maintenance_kcal = weight_kg * kcal_per_kg

    target_info: Dict[str, Any] = {
        "bmi": round(bmi, 2),
        "bmi_category": bmi_cat,
        "maintenance_kcal": int(maintenance_kcal),
        "activity_level": activity_level,
    }

    if goal_weight_kg is not None:
        diff = goal_weight_kg - weight_kg
        direction = "감량" if diff < 0 else "증량" if diff > 0 else "유지"

        safe_weekly = max(0.1, min(abs(weekly_goal_kg), 1.0))
        daily_delta_kcal = safe_weekly * 7700 / 7  # 1kg ≈ 7700kcal
        estimated_weeks = abs(diff) / safe_weekly if safe_weekly > 0 else None

        if direction == "감량":
            target_kcal = max(
                maintenance_kcal - daily_delta_kcal,
                maintenance_kcal * 0.7,
            )
        elif direction == "증량":
            target_kcal = maintenance_kcal + daily_delta_kcal
        else:
            target_kcal = maintenance_kcal

        target_info.update(
            {
                "direction": direction,
                "goal_weight_kg": goal_weight_kg,
                "weekly_goal_kg": safe_weekly,
                "target_kcal": int(target_kcal),
                "estimated_weeks": round(estimated_weeks, 1)
                if estimated_weeks is not None
                else None,
            }
        )

    return target_info


@tool
def nearby_parties(
    user_lat: float,
    user_lon: float,
    max_distance_km: float = 5.0,
    limit: int = 5,
) -> List[Dict[str, Any]]:
    """
    사용자 위치(위도/경도)를 기준으로 근처에서 모집 중인 운동 파티를 조회한다.
    반환값에는 파티 제목, 종목, 장소, 날짜·시간, 거리(km) 등이 포함된다.
    """
    return get_nearby_parties(
        user_lat=user_lat,
        user_lon=user_lon,
        max_distance_km=max_distance_km,
        limit=limit,
    )