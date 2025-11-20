# app/main.py
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import logging

from .schemas import ChatRequest, ChatResponse
from .graph import run_agent
from .weather import get_simple_weather

# 🔽 추가
from .modules.auth.router import router as auth_router  # NEW

logger = logging.getLogger(__name__)

app = FastAPI(
    title="baro",
    version="0.1.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth_router)  # NEW


def is_weather_only_query(msg: str) -> bool:
    """
    '날씨 어때?', '비와?', '눈 와?' 같이 날씨만 물어보는지 판별.
    운동/시설/추천 같은 단어가 같이 있으면 False.
    """
    text = msg.replace(" ", "")

    # 날씨 관련 키워드
    weather_kw = ["날씨", "비와", "눈와", "기온어때", "기온이어때"]
    # 운동/시설 관련 키워드
    sports_kw = ["운동", "헬스장", "수영장", "운동장", "시설", "추천", "코트", "체육관"]

    return any(k in text for k in weather_kw) and not any(
        k in text for k in sports_kw
    )


@app.post("/chatbot/message", response_model=ChatResponse)
def chatbot_message(req: ChatRequest) -> ChatResponse:
    # 1) '날씨만' 묻는 질문이면 여기서 바로 처리하고 끝낸다.
    if is_weather_only_query(req.message):
        if req.user_lat is None or req.user_lon is None:
            raise HTTPException(
                status_code=400,
                detail="날씨를 알려면 user_lat, user_lon(위도/경도)이 필요합니다.",
            )

        info = get_simple_weather(req.user_lat, req.user_lon)
        if info is None:
            return ChatResponse(
                answer="지금은 기상청 날씨 정보를 가져오지 못했어요. 잠시 후 다시 시도해 주세요."
            )

        temp = info["temp_c"]
        cond = info["condition"]  # '맑음', '비', '눈' 등

        if temp is None:
            text = f"현재 하늘 상태는 {cond}입니다."
        else:
            text = f"현재 기온은 약 {temp:.1f}도이고, 하늘 상태는 {cond}입니다."

        # ✅ 이 경우에는 운동 추천 안 하고 날씨만 답변
        return ChatResponse(answer=text)

    # 2) 그 외에는 기존처럼 LangGraph 에이전트에게 넘김 (거리 기반 운동 추천)
    user_text = req.message

    if req.user_lat is not None and req.user_lon is not None:
        location_str = (
            f"[사용자 위치 정보] 위도: {req.user_lat}, 경도: {req.user_lon}\n"
            "이 위치를 기준으로 운동 시설을 추천해줘.\n"
        )
        user_text = location_str + user_text

    try:
        answer = run_agent(user_text)
    except Exception as e:
        logger.exception("Error in /chatbot/message")
        raise HTTPException(status_code=500, detail=str(e))

    return ChatResponse(answer=answer)


@app.get("/")
def health_check():
    return {"status": "ok"}