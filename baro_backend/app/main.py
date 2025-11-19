# app/main.py
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import logging

from .schemas import ChatRequest, ChatResponse
from .common.graph import run_agent

logger = logging.getLogger(__name__)

app = FastAPI(
    title="Songpa Sports Recommendation Chatbot",
    version="0.1.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.post("/chatbot/message", response_model=ChatResponse)
def chatbot_message(req: ChatRequest):
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
        # uvicorn 로그에서 전체 스택을 볼 수 있게
        logger.exception("Error in /chatbot/message")
        # Swagger 응답에도 원인 문자열이 보이게
        raise HTTPException(status_code=500, detail=str(e))

    return ChatResponse(answer=answer)



@app.get("/")
def health_check():
    return {"status": "ok"}