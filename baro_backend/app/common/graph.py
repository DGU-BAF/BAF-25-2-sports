# app/graph.py
from langchain_openai import ChatOpenAI
from langgraph.prebuilt import create_react_agent
from langchain_core.messages import SystemMessage, HumanMessage
import logging

from ..core.config import OPENAI_API_KEY
from .tools import nearby_sports_facilities

logger = logging.getLogger(__name__)

llm = ChatOpenAI(
    model="gpt-4.1-mini",   # 안 되면 "gpt-4o-mini" 같은 걸로 바꿔도 됨
    temperature=0.3,
    api_key=OPENAI_API_KEY,
)

tools = [nearby_sports_facilities]

agent = create_react_agent(llm, tools)

SYSTEM_PROMPT = """
너는 '송파구 거리 기반 운동 추천 챗봇'이다.

역할:
1) 사용자의 위치(위도/경도)와 운동 종류, 실내/실외 선호가 주어지면
   반드시 nearby_sports_facilities 도구를 호출해서
   가까운 순으로 3~5개의 시설을 추천하고,
   각 시설 이름, 운동 종류(ftype_nm), 실내/실외(inout_gbn_nm), 대략적인 거리(km)를
   자연스러운 한국어로 요약해서 알려준다.

2) 사용자가 그냥 운동 관련 상담(운동 방법, 루틴, 스트레칭 등)을 요청하거나
   일반 대화를 할 때는 도구를 쓰지 말고 GPT처럼 자연스럽게 답변한다.

3) 사용자가 위치 정보를 주지 않고 '근처 운동장 추천해줘'라고 하면
   위치(위도/경도)를 먼저 물어본다.
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