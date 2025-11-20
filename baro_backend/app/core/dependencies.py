# app/core/dependencies.py
from __future__ import annotations

from fastapi import Depends, Request

from app.core.security import CurrentUser, parse_bearer_token, get_user_from_token


async def get_current_user(request: Request) -> CurrentUser:
    """
    FastAPI Depends용 '현재 로그인한 사용자' 의존성.

    사용 예:
        @router.get("/me")
        async def get_profile(current_user: CurrentUser = Depends(get_current_user)):
            return current_user
    """
    authorization_header = request.headers.get("Authorization")
    token = parse_bearer_token(authorization_header)
    return get_user_from_token(token)
