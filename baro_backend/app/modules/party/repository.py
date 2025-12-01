# app/modules/party/repository.py
from typing import List, Optional

from .schemas import CreatePartyRequest, Party

# 예시로 Supabase 클라이언트 의존성만 가정
from app.core.supabase import get_supabase_client


class PartyRepository:
    def __init__(self):
        self._client = get_supabase_client()

    # 파티 리스트
    def list_parties(self, user_id: Optional[str]) -> List[Party]:
        # 1. party 테이블에서 row 가져오기
        # 2. party_member 조인해서 current, isJoined 계산
        # 3. Party 스키마로 매핑해서 리턴
        # -> 여기 부분은 실제 테이블 구조에 맞게 작성
        raise NotImplementedError

    def get_party(self, party_id: str, user_id: Optional[str]) -> Party:
        raise NotImplementedError

    def create_party(self, user_id: str, req: CreatePartyRequest) -> Party:
        # 1. party insert (host_id=user_id)
        # 2. party_member에 host row insert
        # 3. Party로 조합해서 리턴
        raise NotImplementedError

    def join_party(self, party_id: str, user_id: str) -> Party:
        raise NotImplementedError

    def leave_party(self, party_id: str, user_id: str) -> Party:
        raise NotImplementedError
