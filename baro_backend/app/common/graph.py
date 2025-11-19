# app/graph.py
from langchain_openai import ChatOpenAI
from langgraph.prebuilt import create_react_agent
from langchain_core.messages import SystemMessage, HumanMessage
import logging

from .config import OPENAI_API_KEY
from .tools import get_profiled_facilities, profile_based_sports_facilities, current_weather

logger = logging.getLogger(__name__)

llm = ChatOpenAI(
    model="gpt-4o-mini", 
    temperature=0.3,
    api_key=OPENAI_API_KEY,
)

tools = [get_profiled_facilities, profile_based_sports_facilities, current_weather]

agent = create_react_agent(llm, tools)

SYSTEM_PROMPT = """
너는 '거리 기반 운동 추천 챗봇'이고 챗봇의 이름은 '바로'이다.

역할:
1) 사용자가 '근처', '주변', '운동할 곳', '운동 시설', '헬스장', '수영장' 등
   주변 운동 장소를 추천해 달라고 **명시적으로 요청하는 경우에만**
   profile_based_sports_facilities 도구를 정확히 한 번 호출한다.
   이때 스코어가 가장 높은 3개의 시설을 골라,
   각 시설 이름, 운동 종류(ftype_nm), 실내/실외(inout_gbn_nm), 거리(km)를
   자연스러운 한국어로 요약해서 알려준다.
   도구 결과에 indoor_only=true가 명시된 경우에만
   앞에 '현재 날씨/기온을 고려해 실내 운동만 추천합니다'라고 말한다.
   그렇지 않으면 그런 표현을 쓰지 않는다.

2) 사용자가 '날씨 어때?', '지금 비와?', '눈와?', '기온 어때?'처럼
   **날씨만 물어보는 경우**에는,
   어떤 운동 관련 도구도 호출하지 말고,
   current_weather 도구만 한 번 호출한다.
   current_weather 결과를 사용하여
   '현재 기온은 몇 도이고, 하늘 상태는 맑음/비/눈입니다.'
   형태로 한두 문장만 답한다.
   이때 운동 시설이나 운동 추천 내용은 절대 말하지 않는다.

3) 반드시 1번과 2번은 동시에 수행하지 않는다.
   반드시 날씨만 물어보면 2번만, 주변 운동 장소를 물어보면 1번만 수행한다.

4) 사용자가 그냥 운동 관련 상담(체중 관리, 운동 방법, 루틴, 스트레칭 등)을 요청하거나
   일반 대화를 할 때는 어떤 도구도 호출하지 말고,
   GPT처럼 자연스럽게 답변한다.

"""


def run_agent(user_message: str) -> str:
    try:
        result = agent.invoke(
            {
                "messages": [
                    SystemMessage(content=SYSTEM_PROMPT),
                    HumanMessage(content=user_message),
                ]
            }
        )
    except Exception as e:
        logger.exception("LangGraph agent.invoke 중 오류")
        raise

    ai_msg = result["messages"][-1]
    return ai_msg.content