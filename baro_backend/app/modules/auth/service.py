# app/modules/auth/service.py
from __future__ import annotations

from typing import Optional
from datetime import datetime

from supabase import Client

from app.core.supabase import get_supabase
from app.modules.auth.schemas import (
    AuthUser,
    SignUpRequest,
    ProfileUpdateRequest,
)

PROFILES_TABLE = "profiles"


def _row_to_auth_user(row: dict) -> AuthUser:
    """
    Supabase profiles row → AuthUser 변환
    """
    return AuthUser(
        id=str(row["id"]),
        kakaoId=row.get("kakao_id"),
        nickname=row["nickname"],
        birthDate=row["birth_date"],          # snake_case 매칭
        gender=row["gender"],
        height=float(row["height"]),
        weight=float(row["weight"]),
        muscleMass=row.get("muscle_mass"),
        skillLevel=row["skill_level"],

        # jsonb로 저장된 배열
        favoriteSports=row.get("favorite_sports", []) or [],

        sportsmanship=float(row.get("sportsmanship", 36.5)),

        latitude=row.get("latitude"),
        longitude=row.get("longitude"),
    )


class AuthService:
    """
    Supabase profiles 테이블 접근
    """

    def __init__(self, client: Optional[Client] = None) -> None:
        self._client: Client = client or get_supabase()

    # ---------------------------------------------------------
    # 내부 헬퍼
    # ---------------------------------------------------------
    def _get_profile_row(self, user_id: str) -> Optional[dict]:
        res = (
            self._client
            .table(PROFILES_TABLE)
            .select("*")
            .eq("id", user_id)
            .single()
            .execute()
        )
        return getattr(res, "data", None)

    # ---------------------------------------------------------
    # 외부 API
    # ---------------------------------------------------------
    def get_profile(self, user_id: str) -> Optional[AuthUser]:
        row = self._get_profile_row(user_id)
        if not row:
            return None
        return _row_to_auth_user(row)

    # ---------------------------------------------------------
    # 회원가입
    # ---------------------------------------------------------
    def create_profile(
        self,
        user_id: str,
        kakao_id: Optional[str],
        signup: SignUpRequest,
    ) -> AuthUser:

        payload = {
            "id": user_id,
            "kakao_id": kakao_id,

            "nickname": signup.nickname,
            "birth_date": signup.birthDate,
            "gender": signup.gender,

            "height": signup.height,
            "weight": signup.weight,
            "muscle_mass": signup.muscleMass,

            "skill_level": signup.skillLevel,

            # jsonb 배열 그대로 저장
            "favorite_sports": signup.favoriteSports,

            # 기본 매너온도
            "sportsmanship": 36.5,

            # created_at        → Supabase default now()
            # location_updated_at → 초기에는 None
        }

        res = (
            self._client
            .table(PROFILES_TABLE)
            .insert(payload)
            .select("*")
            .single()
            .execute()
        )

        return _row_to_auth_user(res.data)

    # ---------------------------------------------------------
    # 프로필 수정
    # ---------------------------------------------------------
    def update_profile(
        self,
        user_id: str,
        update: ProfileUpdateRequest,
    ) -> AuthUser:

        update_dict = {}

        if update.nickname is not None:
            update_dict["nickname"] = update.nickname
        if update.height is not None:
            update_dict["height"] = update.height
        if update.weight is not None:
            update_dict["weight"] = update.weight
        if update.muscleMass is not None:
            update_dict["muscle_mass"] = update.muscleMass
        if update.skillLevel is not None:
            update_dict["skill_level"] = update.skillLevel
        if update.favoriteSports is not None:
            update_dict["favorite_sports"] = update.favoriteSports

        # -----------------------------------------------------
        # 위치 변경 + location_updated_at 갱신
        # -----------------------------------------------------
        location_changed = False

        if update.latitude is not None:
            update_dict["latitude"] = update.latitude
            location_changed = True

        if update.longitude is not None:
            update_dict["longitude"] = update.longitude
            location_changed = True

        if location_changed:
            update_dict["location_updated_at"] = datetime.utcnow().isoformat()

        # -----------------------------------------------------
        # 업데이트할 값이 없다면 그대로 반환
        # -----------------------------------------------------
        if not update_dict:
            row = self._get_profile_row(user_id)
            if not row:
                raise ValueError(f"Profile not found for user_id={user_id}")
            return _row_to_auth_user(row)

        # -----------------------------------------------------
        # DB 업데이트
        # -----------------------------------------------------
        res = (
            self._client
            .table(PROFILES_TABLE)
            .update(update_dict)
            .eq("id", user_id)
            .select("*")
            .single()
            .execute()
        )

        return _row_to_auth_user(res.data)

    # ---------------------------------------------------------
    # ensure_profile: signup 없으면 None
    # ---------------------------------------------------------
    def ensure_profile(
        self,
        user_id: str,
        kakao_id: Optional[str],
        signup: Optional[SignUpRequest] = None,
    ) -> Optional[AuthUser]:

        existing = self.get_profile(user_id)
        if existing:
            return existing

        if signup is None:
            return None

        return self.create_profile(
            user_id=user_id,
            kakao_id=kakao_id,
            signup=signup,
        )
