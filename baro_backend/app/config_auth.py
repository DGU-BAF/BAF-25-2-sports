# app/config_auth.py
from datetime import timedelta
import os

# ⚠️ 실제 서비스에서는 .env에 넣어서 사용
JWT_SECRET_KEY = os.getenv("JWT_SECRET_KEY", "change-me-in-production")
JWT_ALGORITHM = "HS256"
JWT_EXPIRE_DAYS = int(os.getenv("JWT_EXPIRE_DAYS", 7))

KAKAO_USERINFO_URL = "https://kapi.kakao.com/v2/user/me"