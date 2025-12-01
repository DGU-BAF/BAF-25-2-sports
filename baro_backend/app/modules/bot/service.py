from datetime import datetime
import logging

from .graph import run_agent
from .weather import get_simple_weather
from app.modules.bot.schemas import ChatRequest
from openai import RateLimitError

logger = logging.getLogger(__name__)

def calculate_age(birth_date_str: str) -> int:
    """YYYY-MM-DD 문자열을 받아 만 나이를 계산"""
    if not birth_date_str:
        return 0
    try:
        birth = datetime.strptime(birth_date_str, "%Y-%m-%d")
        today = datetime.today()
        return today.year - birth.year - ((today.month, today.day) < (birth.month, birth.day))
    except:
        return 0

def is_weather_only_query(msg: str) -> bool:
    text = msg.replace(" ", "")
    weather_kw = ["날씨", "비와", "눈와", "기온어때", "기온이어때"]
    sports_kw = ["운동", "헬스장", "수영장", "운동장", "시설", "추천", "코트", "체육관"]
    return any(k in text for k in weather_kw) and not any(k in text for k in sports_kw)

def process_bot_message(req: ChatRequest) -> str:
    # 1. 날씨만 묻는 경우 (위치 정보가 있을 때만)
    if is_weather_only_query(req.message) and req.latitude and req.longitude:
        info = get_simple_weather(req.latitude, req.longitude)
        if info is None:
            return "지금은 기상청 날씨 정보를 가져오지 못했어요. 잠시 후 다시 시도해 주세요."
        
        temp = info["temp_c"]
        cond = info["condition"]
        
        if temp is None:
            return f"현재 하늘 상태는 {cond}입니다."
        else:
            return f"현재 기온은 약 {temp:.1f}도이고, 하늘 상태는 {cond}입니다."

    # 2. 챗봇 에이전트에게 전달할 사용자 컨텍스트 생성
    user_context = []

    # (1) 기본 정보
    if req.nickname: user_context.append(f"이름: {req.nickname}")
    if req.gender: user_context.append(f"성별: {req.gender}")
    
    # (2) 나이 계산
    if req.birth_date:
        age = calculate_age(req.birth_date)
        if age > 0: user_context.append(f"나이: {age}세")

    # (3) 신체 정보
    if req.height: user_context.append(f"키: {req.height}cm")
    if req.weight: user_context.append(f"체중: {req.weight}kg")
    if req.muscle_mass: user_context.append(f"골격근량: {req.muscle_mass}kg")

    # (4) 운동 성향
    if req.skill_level: user_context.append(f"운동 숙련도: {req.skill_level}")
    if req.favorite_sports: 
        sports_str = ", ".join(req.favorite_sports)
        user_context.append(f"선호 종목: {sports_str}")

    # (5) 위치 정보
    if req.latitude and req.longitude:
        user_context.append(f"현재 위치(위도: {req.latitude}, 경도: {req.longitude})")

    # 프롬프트 조합
    system_instruction = ""
    if user_context:
        system_instruction = "[사용자 프로필 정보]\n" + "\n".join(user_context) + "\n\n이 정보를 바탕으로 사용자의 질문에 답변해.\n"
    
    # 최종 메시지: 프로필 정보 + 사용자 실제 질문
    final_prompt = system_instruction + req.message

    try:
        return run_agent(final_prompt)
    except RateLimitError:
        logger.warning("OpenAI rate limit exceeded while processing bot message")
        return "지금은 챗봇 서버에 요청이 너무 많아서 잠시 응답을 줄 수 없어요. 잠시 후 다시 시도해 주세요."
    except Exception:
        logger.exception("Unexpected error while calling agent")
        return "챗봇 응답 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
