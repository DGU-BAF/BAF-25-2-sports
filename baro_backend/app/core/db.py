# app/db.py
import math
import requests
from typing import List, Optional, Dict, Any

from .config import SUPABASE_URL, SUPABASE_ANON_KEY

TABLE_NAME = "songpa_sports_data"


def _fetch_all_facilities() -> list[dict]:
    """Supabase REST로 songpa_sports_data 전체 조회"""
    url = f"{SUPABASE_URL}/rest/v1/{TABLE_NAME}"

    params = {
        "select": "faci_cd,faci_nm,faci_addr,faci_lat,faci_lot,ftype_nm,inout_gbn_nm"
    }

    headers = {
        "apikey": SUPABASE_ANON_KEY,
        "Authorization": f"Bearer {SUPABASE_ANON_KEY}",
        # ✅ 실제 스키마 이름으로 지정
        "Accept-Profile": "sports_data",
        # (선택) POST/PUT 쓸 땐 Content-Profile 도 같이 쓰면 좋음
        # "Content-Profile": "sports_data",
    }

    resp = requests.get(url, params=params, headers=headers, timeout=10)

    if not resp.ok:
        raise RuntimeError(
            f"Supabase 요청 실패: {resp.status_code} - {resp.text}"
        )

    return resp.json()


def _haversine(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """위도/경도(도 단위) → 거리(km)."""
    R = 6371.0  # 지구 반지름(km)

    phi1 = math.radians(lat1)
    phi2 = math.radians(lat2)
    dphi = math.radians(lat2 - lat1)
    dlambda = math.radians(lon2 - lon1)

    a = math.sin(dphi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(
        dlambda / 2
    ) ** 2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return R * c


def get_nearby_facilities(
    user_lat: float,
    user_lon: float,
    sports_type: Optional[str] = None,
    inout: Optional[str] = None,
    limit: int = 5,
) -> List[Dict[str, Any]]:
    """
    사용자 위치 기준 가까운 생활체육 시설을 반환.
    - sports_type: ftype_nm(운동 타입)에 포함되는 문자열(예: '농구', '축구')
    - inout: '실내' 또는 '실외'
    """
    rows = _fetch_all_facilities()
    results: List[Dict[str, Any]] = []

    for row in rows:
        try:
            faci_lat = float(row["faci_lat"])
            faci_lon = float(row["faci_lot"])
        except (TypeError, ValueError):
            continue

        # 운동 타입 필터
        if sports_type:
            ftype = (row.get("ftype_nm") or "").lower()
            if sports_type.lower() not in ftype:
                continue

        # 실내/실외 필터
        if inout:
            if inout != row.get("inout_gbn_nm"):
                continue

        distance_km = _haversine(user_lat, user_lon, faci_lat, faci_lon)

        results.append(
            {
                "faci_cd": row["faci_cd"],
                "faci_nm": row["faci_nm"],
                "faci_addr": row["faci_addr"],
                "ftype_nm": row["ftype_nm"],
                "inout_gbn_nm": row["inout_gbn_nm"],
                "faci_lat": faci_lat,
                "faci_lot": faci_lon,
                "distance_km": round(distance_km, 2),
            }
        )

    results.sort(key=lambda x: x["distance_km"])
    return results[:limit]