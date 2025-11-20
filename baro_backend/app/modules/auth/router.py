# app/modules/auth/router.py
from __future__ import annotations

from fastapi import APIRouter, Depends, HTTPException, status
from supabase import Client

from app.core.supabase import get_supabase
from app.core.dependencies import get_current_user
from app.core.security import CurrentUser
from app.modules.auth.schemas import (
    LoginRequest,
    LoginResponse,
    SignUpRequest,
    ProfileUpdateRequest,
    AuthUser,
)
from app.modules.auth.service import AuthService


router = APIRouter(prefix="/auth", tags=["auth"])


# ---------------------------------------------------------
# (1) 카카오 로그인 → Supabase Access Token 발급
# ---------------------------------------------------------
@router.post("/login", response_model=LoginResponse)
async def login_with_kakao(req: LoginRequest):
    supabase: Client = get_supabase()

    # (A) Kakao 검증(현재 Mock)
    kakao_user_id = "mock_kakao_id_" + req.kakaoAccessToken[-6:]

    # (B) Supabase Auth에서 Kakao 로그인 처리
    # supabase-py v2 문법: 반드시 dict를 한 개 넘겨야 함
    try:
        user_res = supabase.auth.sign_in_with_id_token(
            {
                "provider": "kakao",
                "token": req.kakaoAccessToken,
            }
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=f"Supabase 로그인 실패: {e}",
        )

    if not user_res or not user_res.session:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="카카오 인증 또는 Supabase 세션 발급 실패",
        )

    access_token = user_res.session.access_token
    user_id = user_res.user.id

    auth_service = AuthService(supabase)

    # (C) 프로필 조회
    profile = auth_service.get_profile(user_id)

    # (C-1) 프로필 없으면 템플릿 AuthUser 반환 → 클라이언트가 /auth/signup 호출
    if profile is None:
        temp_user = AuthUser(
            id=user_id,
            kakaoId=kakao_user_id,
            nickname="",
            birthDate="",
            gender="",
            height=0,
            weight=0,
            muscleMass=None,
            skillLevel="",
            favoriteSports=[],
            sportsmanship=36.5,
            latitude=None,
            longitude=None,
        )
        return LoginResponse(
            supabaseAccessToken=access_token,
            user=temp_user,
        )

    # (C-2) 기존 프로필 있으면 그대로 반환
    return LoginResponse(
        supabaseAccessToken=access_token,
        user=profile,
    )


# ---------------------------------------------------------
# (2) 회원가입
# ---------------------------------------------------------
@router.post("/signup", response_model=AuthUser)
async def signup(
    data: SignUpRequest,
    current_user: CurrentUser = Depends(get_current_user),
):
    supabase: Client = get_supabase()
    service = AuthService(supabase)

    existed = service.get_profile(current_user.id)
    if existed:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="이미 프로필이 존재하는 사용자입니다.",
        )

    created = service.create_profile(
        user_id=current_user.id,
        kakao_id=current_user.email or None,
        signup=data,
    )
    return created


# ---------------------------------------------------------
# (3) 내 프로필 조회
# ---------------------------------------------------------
@router.get("/me", response_model=AuthUser)
async def get_profile(
    current_user: CurrentUser = Depends(get_current_user),
):
    supabase: Client = get_supabase()
    service = AuthService(supabase)

    profile = service.get_profile(current_user.id)
    if not profile:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="프로필이 존재하지 않습니다. 먼저 /auth/signup 필요",
        )
    return profile


# ---------------------------------------------------------
# (4) 프로필 수정
# ---------------------------------------------------------
@router.patch("/me", response_model=AuthUser)
async def update_profile(
    data: ProfileUpdateRequest,
    current_user: CurrentUser = Depends(get_current_user),
):
    supabase: Client = get_supabase()
    service = AuthService(supabase)

    profile = service.update_profile(
        user_id=current_user.id,
        update=data,
    )
    return profile


# ---------------------------------------------------------
# (디버그용) 특정 user_id 프로필 조회
# ---------------------------------------------------------
@router.get("/debug/{user_id}", response_model=AuthUser)
async def debug_get_profile(user_id: str):
    supabase: Client = get_supabase()
    service = AuthService(supabase)

    profile = service.get_profile(user_id)
    if not profile:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Profile not found for user_id={user_id}",
        )
    return profile
