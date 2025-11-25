# app/auth/routes.py
from typing import Optional

from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from uuid import UUID

from .schemas import (
    LoginRequestDto,
    LoginResponseDto,
    SignUpRequestDto,
    ProfileUpdateRequestDto,
    AuthUser,
)
from .service import (
    login_with_kakao,
    create_jwt_token,
    verify_jwt_token,
    sign_up,
    update_profile,
    get_user,
    delete_user,
)

router = APIRouter()

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="auth/kakao-login")  # 형식상

# -------------------- 공통 인증 의존성 -------------------- #
async def get_current_user_id(token: str = Depends(oauth2_scheme)) -> UUID:
    try:
        user_id = verify_jwt_token(token)
    except ValueError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired token",
        )
    return user_id


# -------------------- 엔드포인트 -------------------- #

@router.post("/auth/kakao-login", response_model=LoginResponseDto)
def kakao_login(req: LoginRequestDto):
    """
    프론트에서 카카오 SDK로 받은 accessToken을 보내면,
    - Kakao /v2/user/me 호출
    - 우리쪽 유저 생성/조회
    - JWT 발급 + AuthUser 리턴
    """
    try:
        user = login_with_kakao(req.kakao_access_token)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

    jwt_token = create_jwt_token(user.id)
    return LoginResponseDto(access_token=jwt_token, user=user)


@router.post("/auth/sign-up", response_model=AuthUser)
def sign_up_endpoint(
    req: SignUpRequestDto,
    user_id: UUID = Depends(get_current_user_id),
):
    """
    최초 회원가입(추가 정보 입력).
    JWT가 있어야 호출 가능.
    """
    user = sign_up(user_id, req)
    return user


@router.get("/users/me", response_model=AuthUser)
def get_my_profile(user_id: UUID = Depends(get_current_user_id)):
    """
    내 프로필 조회 (sportsmanship 포함).
    """
    try:
        user = get_user(user_id)
    except KeyError:
        raise HTTPException(status_code=404, detail="User not found")
    return user


@router.patch("/users/me/profile", response_model=AuthUser)
def update_my_profile(
    req: ProfileUpdateRequestDto,
    user_id: UUID = Depends(get_current_user_id),
):
    """
    프로필 수정 (nullable 필드만 업데이트).
    """
    user = update_profile(user_id, req)
    return user


@router.post("/auth/logout")
def logout(user_id: UUID = Depends(get_current_user_id)):
    """
    서버에서 특별히 할 일은 없고,
    프론트에서 JWT 삭제하면 됨.
    필요하면 블랙리스트 관리 로직 추가 가능.
    """
    return {"detail": "logged out"}


@router.delete("/users/me")
def delete_account(user_id: UUID = Depends(get_current_user_id)):
    """
    탈퇴: 유저 삭제 + 세션 정리(프론트에서 토큰 삭제).
    """
    delete_user(user_id)
    return {"detail": "account deleted"}