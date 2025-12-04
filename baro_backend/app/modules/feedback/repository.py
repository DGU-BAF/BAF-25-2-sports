# app/modules/feedback/repository.py
from __future__ import annotations

from datetime import datetime, timedelta, timezone
from typing import List

from app.core.supabase import get_supabase_client

from .schemas import (
    FeedbackStatus,
    MyPartyFeedback,
    FeedbackTarget,
    MemberRating,
)

FEEDBACK_AVAILABLE_DAYS = 2  # 파티 종료 후 7일 동안 평가 가능 (필요하면 조정)


class FeedbackRepository:
    def __init__(self) -> None:
        self._client = get_supabase_client()

    # 1) 내가 참여한 파티 + 피드백 상태
    def get_my_parties(self, user_id: str) -> List[MyPartyFeedback]:
        # party_member 에서 내가 참여한 party_id 모으기
        membership_res = (
            self._client.table("app.party_member")
            .select("party_id")
            .eq("user_id", user_id)
            .execute()
        )
        if membership_res.error:
            raise RuntimeError(f"Supabase error (party_member): {membership_res.error}")

        party_ids = [row["party_id"] for row in membership_res.data]
        if not party_ids:
            return []

        # party 정보
        party_res = (
            self._client.table("app.party")
            .select("id, title, date, end_at")
            .in_("id", party_ids)
            .execute()
        )
        if party_res.error:
            raise RuntimeError(f"Supabase error (party): {party_res.error}")

        # 이 유저가 이미 피드백을 '한' 파티들(from_user_id 사용)
        feedback_res = (
            self._client.table("app.feedback")
            .select("party_id")
            .eq("from_user_id", user_id)
            .execute()
        )
        if feedback_res.error:
            raise RuntimeError(f"Supabase error (feedback submitted): {feedback_res.error}")

        submitted_party_ids = {row["party_id"] for row in feedback_res.data}

        now = datetime.now(timezone.utc)
        items: List[MyPartyFeedback] = []

        for row in party_res.data:
            party_id = row["id"]
            title = row.get("title") or ""
            date_str = row.get("date")
            end_at_str = row.get("end_at")

            try:
                end_at = datetime.fromisoformat(end_at_str.replace("Z", "+00:00"))
            except Exception:
                end_at = now - timedelta(days=9999)

            if party_id in submitted_party_ids:
                status = FeedbackStatus.SUBMITTED
            else:
                if end_at <= now <= end_at + timedelta(days=FEEDBACK_AVAILABLE_DAYS):
                    status = FeedbackStatus.AVAILABLE
                else:
                    status = FeedbackStatus.EXPIRED

            items.append(
                MyPartyFeedback(
                    party_id=party_id,
                    title=title,
                    date=date_str,
                    end_at=end_at_str,
                    feedback_status=status,
                )
            )

        items.sort(key=lambda x: x.end_at, reverse=True)
        return items

    # 2) 특정 파티에 대해 내가 평가할 대상 멤버
    def get_feedback_targets(self, party_id: str, current_user_id: str) -> List[FeedbackTarget]:
        # 파티 멤버 조회
        member_res = (
            self._client.table("app.party_member")
            .select("user_id")
            .eq("party_id", party_id)
            .execute()
        )
        if member_res.error:
            raise RuntimeError(f"Supabase error (party_member for targets): {member_res.error}")

        member_ids = {
            row["user_id"] for row in member_res.data
            if row["user_id"] != current_user_id  # 본인은 제외
        }
        if not member_ids:
            return []

        # 멤버 프로필 조회 (닉네임 + 현재 스포츠맨십)
        profile_res = (
            self._client.table("app.user_profile")
            .select("id, nickname, sportsmanship")
            .in_("id", list(member_ids))
            .execute()
        )
        if profile_res.error:
            raise RuntimeError(f"Supabase error (user_profile for targets): {profile_res.error}")

        targets: List[FeedbackTarget] = []
        for row in profile_res.data:
            targets.append(
                FeedbackTarget(
                    user_id=row["id"],
                    nickname=row.get("nickname") or "알 수 없음",
                    sportsmanship=row.get("sportsmanship"),
                )
            )

        return targets

    # 3) 피드백 submit + (선택) 스포츠맨십 스코어 갱신
    def submit_feedback(
        self,
        party_id: str,
        rater_id: str,
        ratings: List[MemberRating],
    ) -> None:
        if not ratings:
            return

        # 이 유저가 해당 파티 멤버인지 확인
        membership_res = (
            self._client.table("app.party_member")
            .select("party_id")
            .eq("party_id", party_id)
            .eq("user_id", rater_id)
            .single()
            .execute()
        )
        if membership_res.error:
            raise RuntimeError("해당 파티에 참여하지 않은 유저는 피드백을 남길 수 없습니다.")

        # feedback 테이블에 insert/upsert
        rows_to_upsert = []
        for rating in ratings:
            rows_to_upsert.append(
                {
                    "party_id": party_id,
                    "from_user_id": rater_id,
                    "to_user_id": rating.user_id,
                    "score": rating.rating,
                    # created_at은 Supabase default now() 쓰면 굳이 안 넣어도 됨
                }
            )

        feedback_res = (
            self._client.table("app.feedback")
            .upsert(rows_to_upsert)  # (party_id, from_user_id, to_user_id) 에 unique 제약 추천
            .execute()
        )
        if feedback_res.error:
            raise RuntimeError(f"Supabase error (upsert feedback): {feedback_res.error}")

        # 각 멤버의 sportsmanship 재계산(선택)
        for rating in ratings:
            self._recalculate_sportsmanship(rating.user_id)

    def _recalculate_sportsmanship(self, user_id: str) -> None:
        # 해당 유저(to_user_id)가 받은 모든 score 평균을 sportsmanship 에 반영
        res = (
            self._client.table("app.feedback")
            .select("score")
            .eq("to_user_id", user_id)
            .execute()
        )
        if res.error or not res.data:
            return

        scores = [row["score"] for row in res.data if row.get("score") is not None]
        if not scores:
            return

        avg_score = round(sum(scores) / len(scores))

        update_res = (
            self._client.table("app.user_profile")
            .update({"sportsmanship": int(avg_score)})
            .eq("id", user_id)
            .execute()
        )
        _ = update_res  # 에러 나도 전체 플로우는 막지 않음
