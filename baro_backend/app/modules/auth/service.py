# app/auth/service.py
from __future__ import annotations

from datetime import datetime, timedelta, timezone
from typing import Dict, Optional, Any, List
from uuid import UUID

import requests
from jose import jwt, JWTError

from .schemas import AuthUser, SignUpRequestDto, ProfileUpdateRequestDto
from app.config import (
    SUPABASE_URL,
    SUPABASE_ANON_KEY,
    SUPABASE_AUTH_SCHEMA,      # config.py에서 가져오도록 이동
    SUPABASE_USERS_TABLE,      # config.py에서 가져오도록 이동
    SUPABASE_PROFILES_TABLE,   # config.py에서 가져오도록 이동
)
from app.config_auth import (
    JWT_SECRET_KEY,
    JWT_ALGORITHM,
    JWT_EXPIRE_DAYS,
    KAKAO_USERINFO_URL,
)

# -------------------- 공통 Supabase 헬퍼 -------------------- #

def _sb_headers() -> Dict[str, str]:
    return {
        "apikey": SUPABASE_ANON_KEY,
        "Authorization": f"Bearer {SUPABASE_ANON_KEY}",
        "Accept-Profile": SUPABASE_AUTH_SCHEMA,
        "Content-Type": "application/json",
    }


def _sb_get(table: str, params: Dict[str, Any]) -> List[Dict[str, Any]]:
    url = f"{SUPABASE_URL}/rest/v1/{table}"
    resp = requests.get(url, params=params, headers=_sb_headers(), timeout=10)
    if not resp.ok:
        raise RuntimeError(f"Supabase GET 실패: {resp.status_code} - {resp.text}")
    return resp.json()


def _sb_post(table: str, body: Dict[str, Any]) -> Dict[str, Any]:
    url = f"{SUPABASE_URL}/rest/v1/{table}"
    headers = _sb_headers()
    headers["Prefer"] = "return=representation"
    resp = requests.post(url, json=body, headers=headers, timeout=10)
    if not resp.ok:
        raise RuntimeError(f"Supabase POST 실패: {resp.status_code} - {resp.text}")
    data = resp.json()
    return data[0] if data else {}


def _sb_patch(table: str, match: Dict[str, str], body: Dict[str, Any]) -> Dict[str, Any]:
    url = f"{SUPABASE_URL}/rest/v1/{table}"

    params = match.copy()
    headers = _sb_headers()
    headers["Prefer"] = "return=representation"
    resp = requests.patch(url, params=params, json=body, headers=headers, timeout=10)
    if not resp.ok:
        raise RuntimeError(f"Supabase PATCH 실패: {resp.status_code} - {resp.text}")
    data = resp.json()
    return data[0] if data else {}


def _sb_delete(table: str, match: Dict[str, str]) -> None:
    url = f"{SUPABASE_URL}/rest/v1/{table}"
    params = match.copy()
    resp = requests.delete(url, params=params, headers=_sb_headers(), timeout=10)
    if not resp.ok:
        raise RuntimeError(f"Supabase DELETE 실패: {resp.status_code} - {resp.text}")


# -------------------- Kakao API -------------------- #

def get_kakao_profile(access_token: str) -> dict:
    """
    카카오 access token으로 /v2/user/me 호출해서 프로필 가져오기.
    """
    headers = {"Authorization": f"Bearer {access_token}"}
    resp = requests.get(KAKAO_USERINFO_URL, headers=headers, timeout=5)
    if not resp.ok:
        raise ValueError(f"Kakao API error: {resp.status_code} - {resp.text}")

    return resp.json()


# -------------------- JWT -------------------- #

def create_jwt_token(user_id: UUID) -> str:
    exp = datetime.now(timezone.utc) + timedelta(days=JWT_EXPIRE_DAYS)
    payload = {
        "sub": str(user_id),
        "exp": exp,
    }
    token = jwt.encode(payload, JWT_SECRET_KEY, algorithm=JWT_ALGORITHM)
    return token


def verify_jwt_token(token: str) -> UUID:
    try:
        payload = jwt.decode(token, JWT_SECRET_KEY, algorithms=[JWT_ALGORITHM])
        sub = payload.get("sub")
        if sub is None:
            raise JWTError("No sub")
        return UUID(sub)
    except JWTError as e:
        raise ValueError("Invalid token") from e


# -------------------- Supabase users / user_profiles -------------------- #

def _get_user_row_by_kakao(kakao_id: str) -> Optional[Dict[str, Any]]:
    rows = _sb_get(
        SUPABASE_USERS_TABLE,
        {
            "select": "id,kakao_id",
            "kakao_id": f"eq.{kakao_id}",
            "limit": 1,
        },
    )
    return rows[0] if rows else None


def _get_user_row_by_id(user_id: UUID) -> Dict[str, Any]:
    rows = _sb_get(
        SUPABASE_USERS_TABLE,
        {
            "select": "id,kakao_id",
            "id": f"eq.{user_id}",
            "limit": 1,
        },
    )
    if not rows:
        raise KeyError("user not found")
    return rows[0]


def _insert_user_row(kakao_id: str) -> Dict[str, Any]:
    return _sb_post(
        SUPABASE_USERS_TABLE,
        {
            "kakao_id": kakao_id,
        },
    )


def _get_profile_row(user_id: UUID) -> Optional[Dict[str, Any]]:
    rows = _sb_get(
        SUPABASE_PROFILES_TABLE,
        {
            "select": "*",
            "user_id": f"eq.{user_id}",
            "limit": 1,
        },
    )
    return rows[0] if rows else None


def _upsert_profile_row(user_id: UUID, body: Dict[str, Any]) -> Dict[str, Any]:
    """
    user_profiles 에 row가 있으면 PATCH, 없으면 INSERT.
    """
    existing = _get_profile_row(user_id)
    body_with_id = body.copy()
    body_with_id["user_id"] = str(user_id)

    if existing:
        return _sb_patch(
            SUPABASE_PROFILES_TABLE,
            {"user_id": f"eq.{user_id}"},
            body_with_id,
        )
    else:
        return _sb_post(SUPABASE_PROFILES_TABLE, body_with_id)


def _row_to_auth_user(
    user_row: Dict[str, Any],
    profile_row: Optional[Dict[str, Any]],
    kakao_nickname: Optional[str] = None,
) -> AuthUser:
    """
    Supabase users + user_profiles → AuthUser 로 매핑.
    profile_row 가 없을 수도 있음.
    """
    profile_row = profile_row or {}

    # preferred_sports 가 text[] 인 경우와 text (콤마 구분) 인 경우 모두 고려
    pref = profile_row.get("preferred_sports")
    if isinstance(pref, str):
        preferred_sports = [s.strip() for s in pref.split(",") if s.strip()]
    else:
        preferred_sports = pref

    return AuthUser(
        id=UUID(user_row["id"]),
        kakao_id=user_row["kakao_id"],
        nickname=profile_row.get("nickname") or kakao_nickname,
        age=profile_row.get("age"),
        gender=profile_row.get("gender"),
        height_cm=profile_row.get("height_cm"),
        weight_kg=profile_row.get("weight_kg"),
        level=profile_row.get("level"),
        preferred_sports=preferred_sports,
        latitude=profile_row.get("latitude"),
        longitude=profile_row.get("longitude"),
        sportsmanship=profile_row.get("sportsmanship"),
    )


# -------------------- 외부에서 쓰는 서비스 함수들 -------------------- #

def login_with_kakao(access_token: str) -> AuthUser:
    """
    1) 카카오에서 프로필 조회
    2) kakao_id 기준으로 Supabase users 에서 유저 생성/조회
    3) user_profiles 와 합쳐서 AuthUser 리턴
    """
    data = get_kakao_profile(access_token)

    kakao_id = str(data["id"])
    profile = data.get("kakao_account", {}).get("profile", {})

    nickname = profile.get("nickname")

    # 1) users 테이블에서 kakao_id 로 검색
    user_row = _get_user_row_by_kakao(kakao_id)
    if not user_row:
        # 신규 가입 → users 에 row 만들기
        user_row = _insert_user_row(kakao_id)

    # 2) user_profiles 조회
    user_id = UUID(user_row["id"])
    profile_row = _get_profile_row(user_id)

    # 3) AuthUser 로 매핑 (profile 없는 경우도 허용)
    return _row_to_auth_user(user_row, profile_row, nickname)


def sign_up(user_id: UUID, req: SignUpRequestDto) -> AuthUser:
    """
    최초 회원가입(추가 정보 입력) → user_profiles upsert.
    """
    body = req.dict(exclude_none=True)
    profile_row = _upsert_profile_row(user_id, body)
    user_row = _get_user_row_by_id(user_id)
    return _row_to_auth_user(user_row, profile_row)


def update_profile(user_id: UUID, req: ProfileUpdateRequestDto) -> AuthUser:
    """
    프로필 수정 → user_profiles upsert.
    """
    body = req.dict(exclude_unset=True, exclude_none=True)
    if not body:
        # 수정할 내용이 없으면 그대로 반환
        profile_row = _get_profile_row(user_id)
    else:
        profile_row = _upsert_profile_row(user_id, body)

    user_row = _get_user_row_by_id(user_id)
    return _row_to_auth_user(user_row, profile_row)


def get_user(user_id: UUID) -> AuthUser:
    user_row = _get_user_row_by_id(user_id)
    profile_row = _get_profile_row(user_id)
    return _row_to_auth_user(user_row, profile_row)


def delete_user(user_id: UUID) -> None:
    # profile 먼저 삭제 후 user 삭제
    _sb_delete(SUPABASE_PROFILES_TABLE, {"user_id": f"eq.{user_id}"})
    _sb_delete(SUPABASE_USERS_TABLE, {"id": f"eq.{user_id}"})
