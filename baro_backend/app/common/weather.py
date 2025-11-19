# app/weather.py
import requests
from datetime import datetime, timedelta
from typing import Tuple, Optional, Dict, Any
import logging

from .config import KMA_API_KEY

logger = logging.getLogger(__name__)

# 온도 기준: 0도 이하 / 30도 이상이면 실내만 추천
TOO_COLD_C = 0.0
TOO_HOT_C = 30.0


def _latlon_to_nxny(lat: float, lon: float) -> Tuple[int, int]:
    """
    기상청 격자 좌표(nx, ny)로 변환.
    지금은 서비스 범위를 송파구로 가정하고 송파구 격자를 상수로 사용.
    (실제 nx, ny 값은 송파구 기준으로 맞춰서 사용하면 됨)
    """
    NX_SONGPA = 62  # 송파구에 맞는 값으로 세팅한 상태라고 가정
    NY_SONGPA = 126
    return NX_SONGPA, NY_SONGPA


def _current_base_datetime() -> Tuple[str, str]:
    """
    초단기실황(getUltraSrtNcst)용 base_date, base_time 계산.
    - 매시간 XX:40 이후에 직전 정시 데이터가 갱신되므로
      40분 전이면 한 시간 전 정시를, 그 이후면 현재 정시를 사용.
    """
    now = datetime.utcnow() + timedelta(hours=9)  # KST
    if now.minute < 40:
        base_dt = now - timedelta(hours=1)
    else:
        base_dt = now
    base_date = base_dt.strftime("%Y%m%d")
    base_time = base_dt.strftime("%H00")
    return base_date, base_time


def is_indoor_only(lat: float, lon: float) -> bool:
    """
    기상청 API(초단기실황)를 이용해
    - 비/눈(PTY 1,2,3)이 오거나
    - 기온(T1H)이 너무 춥거나/더우면
    True를 반환 → 실내 운동만 추천.
    """
    if not KMA_API_KEY:
        # 키 없으면 날씨 기반 필터 비활성
        logger.warning("[weather] KMA_API_KEY not set, skipping weather check.")
        return False

    nx, ny = _latlon_to_nxny(lat, lon)
    base_date, base_time = _current_base_datetime()

    # ✅ 초단기실황 API 사용 (T1H, PTY가 확실히 내려옴)
    url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"
    params = {
        "serviceKey": KMA_API_KEY,
        "numOfRows": 100,
        "pageNo": 1,
        "dataType": "JSON",
        "base_date": base_date,
        "base_time": base_time,
        "nx": nx,
        "ny": ny,
    }

    try:
        resp = requests.get(url, params=params, timeout=5)
        resp.raise_for_status()
        data = resp.json()
    except Exception as e:
        logger.warning(f"[weather] API error: {e}")
        # API 오류 나면 날씨 필터 없이 진행
        return False

    items = (
        data.get("response", {})
        .get("body", {})
        .get("items", {})
        .get("item", [])
    )

    if not items:
        logger.warning("[weather] No items in KMA response.")
        return False

    temp_c = None
    pty = 0  # 0: 없음, 1: 비, 2: 비/눈, 3: 눈

    for it in items:
        cat = it.get("category")
        val = it.get("obsrValue")
        if val is None:
            continue

        try:
            if cat == "T1H":  # 기온
                temp_c = float(val)
            elif cat == "PTY":  # 강수형태
                pty = int(float(val))
        except (TypeError, ValueError):
            # 값 이상하면 무시
            continue

    is_rain_or_snow = pty in (1, 2, 3)

    is_extreme_temp = False
    if temp_c is not None:
        is_extreme_temp = (temp_c <= TOO_COLD_C) or (temp_c >= TOO_HOT_C)

    indoor_only = is_rain_or_snow or is_extreme_temp

    print(
        f"[weather-debug] base={base_date}{base_time}, temp={temp_c}, pty={pty}, "
        f"is_rain_or_snow={is_rain_or_snow}, "
        f"is_extreme_temp={is_extreme_temp}, "
        f"indoor_only={indoor_only}",
        flush=True,
    )
    logger.info(
        f"[weather] base={base_date}{base_time}, temp={temp_c}, pty={pty}, indoor_only={indoor_only}"
    )

    return indoor_only


def get_simple_weather(lat: float, lon: float) -> Optional[Dict[str, Any]]:
    """
    사용자 위치 기준 현재 날씨를 간단히 리턴.
    - temp_c: 현재 기온 (°C)
    - condition: '맑음' / '비' / '눈' / '비 또는 눈' / '알 수 없음'
    """
    if not KMA_API_KEY:
        return None

    # 👉 is_indoor_only에서 쓰던 것과 같은 방식으로 호출
    nx, ny = _latlon_to_nxny(lat, lon)

    # 현재 시간 기준 base_date/base_time 계산 (초단기실황 규칙)
    now = datetime.utcnow() + timedelta(hours=9)  # KST
    if now.minute < 40:
        base_dt = now - timedelta(hours=1)
    else:
        base_dt = now
    base_date = base_dt.strftime("%Y%m%d")
    base_time = base_dt.strftime("%H00")

    url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"
    params = {
        "serviceKey": KMA_API_KEY,
        "numOfRows": 100,
        "pageNo": 1,
        "dataType": "JSON",
        "base_date": base_date,
        "base_time": base_time,
        "nx": nx,
        "ny": ny,
    }

    try:
        resp = requests.get(url, params=params, timeout=5)
        resp.raise_for_status()
        data = resp.json()
    except Exception as e:
        logger.warning(f"[weather-simple] API error: {e}")
        return None

    items = (
        data.get("response", {})
        .get("body", {})
        .get("items", {})
        .get("item", [])
    )
    if not items:
        return None

    temp_c = None
    pty = 0  # 0: 없음, 1: 비, 2: 비/눈, 3: 눈

    for it in items:
        cat = it.get("category")
        val = it.get("obsrValue")
        if val is None:
            continue
        try:
            if cat == "T1H":
                temp_c = float(val)
            elif cat == "PTY":
                pty = int(float(val))
        except (TypeError, ValueError):
            continue

    # 강수형태 → 하늘 상태 텍스트
    if pty == 0:
        condition = "맑음"
    elif pty == 1:
        condition = "비"
    elif pty == 2:
        condition = "비 또는 눈"
    elif pty == 3:
        condition = "눈"
    else:
        condition = "알 수 없음"

    print(
        f"[weather-simple] base={base_date}{base_time}, temp={temp_c}, pty={pty}, condition={condition}",
        flush=True,
    )

    return {
        "temp_c": temp_c,
        "condition": condition,
        "base_date": base_date,
        "base_time": base_time,
    }