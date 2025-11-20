# app/modules/auth/schemas.py
from __future__ import annotations

from typing import List, Optional
from pydantic import BaseModel, Field


# ---------------------------------------------------------
# 기본 사용자 모델 (안드로이드 AuthUser 대응)
# ---------------------------------------------------------
class AuthUser(BaseModel):
    id: str                                   # Supabase user UUID
    kakaoId: Optional[str] = None             # 카카오 계정 식별자 (읽기 전용)

    nickname: str
    birthDate: str                            # "YYYY-MM-DD"
    gender: str

    height: float                             # cm
    weight: float                             # kg
    muscleMass: Optional[float] = None        # 골격근량

    skillLevel: str                           # beginner / intermediate / advanced
    favoriteSports: List[str]

    sportsmanship: float                      # 서버 계산값 (클라이언트 수정 불가)

    latitude: Optional[float] = None          # 마지막 위치
    longitude: Optional[float] = None
    


# ---------------------------------------------------------
# 회원가입 요청 (안드로이드 SignUpRequest 대응)
# ---------------------------------------------------------
class SignUpRequest(BaseModel):
    nickname: str
    birthDate: str
    gender: str

    height: float
    weight: float
    muscleMass: Optional[float] = None

    skillLevel: str
    favoriteSports: List[str]


# ---------------------------------------------------------
# 프로필 수정 요청 (안드로이드 ProfileUpdateRequest 대응)
# ---------------------------------------------------------
class ProfileUpdateRequest(BaseModel):
    nickname: Optional[str] = None
    height: Optional[float] = None
    weight: Optional[float] = None
    muscleMass: Optional[float] = None

    skillLevel: Optional[str] = None
    favoriteSports: Optional[List[str]] = None

    latitude: Optional[float] = None
    longitude: Optional[float] = None


# ---------------------------------------------------------
# 로그인 요청/응답 (카카오 액세스 토큰 기반)
# ---------------------------------------------------------
class LoginRequest(BaseModel):
    kakaoAccessToken: str = Field(..., description="카카오 사용자 액세스 토큰")


class LoginResponse(BaseModel):
    """
    - supabaseAccessToken: 안드로이드가 이후 모든 요청에 Bearer로 넣어줄 토큰
    - user: 프로필 정보 반환
    """
    supabaseAccessToken: str
    user: AuthUser
