# app/core/security.py
from __future__ import annotations

from typing import Optional

from fastapi import HTTPException, status
from pydantic import BaseModel

from app.core.supabase import get_supabase


class CurrentUser(BaseModel):
    """
    인증이 통과된 '현재 사용자' 정보를 표현하는 모델.
    필요한 필드만 최소한으로 둔 후, 나중에 확장해도 됨.
    """
    id: str
    email: Optional[str] = None


def parse_bearer_token(authorization_header: Optional[str]) -> str:
    """
    Authorization 헤더에서 'Bearer <token>' 형식의 토큰을 추출한다.
    형식이 잘못되었거나 비어 있으면 401 에러를 일으킨다.
    """
    if not authorization_header:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Authorization 헤더가 필요합니다.",
        )

    parts = authorization_header.split()

    if len(parts) != 2 or parts[0].lower() != "bearer":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Authorization 헤더 형식이 잘못되었습니다. 'Bearer <token>' 형식을 사용하세요.",
        )

    token = parts[1].strip()
    if not token:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="토큰이 비어 있습니다.",
        )

    return token


def get_user_from_token(access_token: str) -> CurrentUser:
    """
    Supabase Auth에 access_token(JWT)을 전달해 유효한 사용자 정보를 조회한다.

    - 토큰이 유효하지 않으면 401 에러
    - 정상일 경우 CurrentUser로 변환해 반환
    """
    supabase = get_supabase()

    try:
        # supabase-py v2 기준: auth.get_user(access_token)
        res = supabase.auth.get_user(access_token)
        user = res.user  # supabase.auth.user 객체 (또는 dict 비슷한 구조)

    except Exception:
        # 구체적인 예외 타입을 잡고 싶으면 supabase 패키지의 에러 타입을 추가로 import 해서 사용
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="유효하지 않은 토큰입니다.",
        )

    if user is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="유효하지 않은 사용자입니다.",
        )

    # user.id / user.email 속성 이름은 supabase-py 버전에 따라 다를 수 있으므로,
    # 실제 객체 구조에 맞게 필요 시 수정
    user_id = getattr(user, "id", None) or getattr(user, "user_id", None)
    email = getattr(user, "email", None)

    if not user_id:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="사용자 ID를 찾을 수 없습니다.",
        )

    return CurrentUser(id=str(user_id), email=email)
