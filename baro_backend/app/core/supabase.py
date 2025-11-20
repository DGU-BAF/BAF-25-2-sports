# app/core/supabase.py
from __future__ import annotations

from typing import Optional

from supabase import Client, create_client

from app.config import SUPABASE_URL, SUPABASE_KEY

_supabase_client: Optional[Client] = None


def get_supabase() -> Client:
    """
    애플리케이션 전체에서 공유하는 Supabase 클라이언트.

    - 처음 호출될 때만 create_client()로 생성
    - 이후에는 같은 인스턴스를 재사용
    """
    global _supabase_client

    if _supabase_client is None:
        if not SUPABASE_URL or not SUPABASE_KEY:
            raise RuntimeError(
                "Supabase 설정이 없습니다. .env와 app.config를 확인하세요 "
                "(SUPABASE_URL / SUPABASE_KEY)."
            )

        _supabase_client = create_client(SUPABASE_URL, SUPABASE_KEY)

    return _supabase_client
