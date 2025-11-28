from pydantic import BaseModel, Field
from typing import Optional, List
from uuid import UUID
from datetime import datetime

# 로그인 요청
class LoginRequestDto(BaseModel):
    # 프론트엔드에서는 kakaoAccessToken으로 보내지만, 
    # Python 네이밍 컨벤션(snake_case)에 맞추거나 alias를 사용합니다.
    kakao_access_token: str = Field(..., alias="kakaoAccessToken")

    class Config:
        populate_by_name = True

# 사용자 정보 모델
class AuthUser(BaseModel):
    id: UUID
    kakao_id: str
    nickname: Optional[str] = None
    role: str = "user"  # 기본값 user
    
    # 추가 프로필 정보
    age: Optional[int] = None
    gender: Optional[str] = None
    height_cm: Optional[float] = None
    weight_kg: Optional[float] = None
    level: Optional[str] = None
    preferred_sports: Optional[List[str]] = []
    latitude: Optional[float] = None
    longitude: Optional[float] = None
    sportsmanship: Optional[float] = None

# 로그인 응답
class LoginResponseDto(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user: AuthUser

# 회원가입(추가 정보 입력) 요청
class SignUpRequestDto(BaseModel):
    nickname: Optional[str] = None
    age: Optional[int] = None
    gender: Optional[str] = None
    height_cm: Optional[float] = None
    weight_kg: Optional[float] = None
    level: Optional[str] = None
    preferred_sports: Optional[List[str]] = []
    latitude: Optional[float] = None
    longitude: Optional[float] = None

# 프로필 수정 요청
class ProfileUpdateRequestDto(BaseModel):
    nickname: Optional[str] = None
    age: Optional[int] = None
    height_cm: Optional[float] = None
    weight_kg: Optional[float] = None
    level: Optional[str] = None
    preferred_sports: Optional[List[str]] = None
    latitude: Optional[float] = None
    longitude: Optional[float] = None