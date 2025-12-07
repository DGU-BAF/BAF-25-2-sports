# 바로 ⚡️
바로는 내 주변에서 나에게 맞는 운동을 빠르게 추천할 수 있는 챗봇 기능과 내 주변에서 운동을 같이 할 사람을 모집하는 기능이 앱입니다.
비어플 2025년 2학기 프로젝트 스포츠팀에서 개발했으며, 송파구 지역을 한정으로 서비스 합니다.

## 주요 기능
1. **파티 매칭**
    - 여러 사람과 함께 운동할 수 있다.
        - 다른 사용자가 만든 파티에 자유롭게 참여할 수 있다.
        - 사용자가 직접 파티를 생성하여 함께 운동할 사람을 모집할 수 있다.
    - 파티 참여 전, 파티 멤버들의 닉네임과 스포츠맨십 점수를 확인할 수 있다.
    - 파티 종료 후 48시간 내에 상대의 스포츠맨십을 평가하는 피드백 기능을 제공한다.
        - 피드백은 별점 1~5개로 구성되며, 상대의 스포츠맨십 점수에 직접 반영된다.
        - 스포츠맨십 점수가 공개되므로, 사용자가 파티 참여에 느끼는 부담을 줄일 수 있다.
    - 내가 참여한 파티에서는 메시지 기능을 통해 파티원들과 소통할 수 있다.

2. **챗봇 기능**
    

## 기술 스택
**앱 디자인: Figma**
- 전체 UI/UX 설계
- 사용자 흐름(Flow) 구성 및 프로토타이핑 진행

**모바일 앱(Android) : Android Studio**
- Kotlin: 앱 로직 및 MVVM 아키텍처 구현
- XML: 화면 레이아웃 및 UI 구성
- Retrofit, Coroutine 기반 네트워크 및 상태 관리

**백엔드 서버**
- FastAPI

**데이터베이스**
- Supabase

## 폴더 구조
```plaintext
Baro/
├── baro_frontend/                               # Android 앱 (Kotlin/XML)
│   ├── app/
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/com/example/baro/
│   │   │       │   ├── core/                   # 공통 네트워크, DI, 유틸, 데이터스토어 등 핵심 인프라
│   │   │       │   ├── feature/                # 기능(화면) 단위 모듈
│   │   │       │   │   ├── auth/               # 카카오 로그인, 회원가입, 프로필 설정
│   │   │       │   │   ├── bot/                # GPT 기반 운동 추천 챗봇 UI 및 로직
│   │   │       │   │   ├── feedback/           # 파티 후 스포츠맨십 평가(별점) 기능
│   │   │       │   │   ├── home/               # 홈 탭(파티 탐색, 생성 화면)
│   │   │       │   │   ├── message/            # 파티 채팅 메시지 기능
│   │   │       │   │   ├── party/              # 파티 상세, 참여, 생성, 리스트 관리
│   │   │       │   │   └── select/             # 운동 종목·취향 선택 화면
│   │   │       │   │
│   │   │       │   ├── GlobalApplications.kt   # 앱 전역 초기화 설정(Application 클래스)
│   │   │       │   └── MainActivity.kt         # 앱의 최상위 Activity, 네비게이션 호스트
│   │   │       │
│   │   │       ├── res/                        # XML 레이아웃 및 UI 리소스
│   │   │       │   ├── color/                  # 색상 리소스
│   │   │       │   ├── drawable/               # 아이콘, 배경 등 그래픽 리소스
│   │   │       │   ├── font/                   # 폰트 리소스
│   │   │       │   ├── layout/                 # 화면 XML 레이아웃 파일
│   │   │       │   └── values/                 # strings.xml, styles.xml 등 공통 설정
│   │   │       │
│   │   │       └── AndroidManifest.xml         # 앱 구성(권한, Activity 등) 설정 파일
│   │   │
│   │   └── build.gradle.kts                    # 모듈 Gradle 설정(Kotlin DSL)
│   │
│   └── gradle/                                 # Gradle 래퍼 및 설정
│       └── libs.versions.toml                  # 버전 카탈로그(의존성 버전 관리)
│
├── baro_backend/                               # FastAPI 기반 백엔드 서버
│   ├── app/
│   │   ├── core/                               # 핵심 인프라 및 설정
│   │   │   └── supabase.py                     # Supabase 클라이언트 및 DB 연결 설정
│   │   │
│   │   ├── modules/                            # 도메인별 기능 모듈 (Router-Service-Repo 구조)
│   │   │   ├── auth/                           # 사용자 인증 (JWT, OAuth)
│   │   │   │   ├── deps.py                     # 의존성 주입 (현재 로그인 유저 확인 등)
│   │   │   │   ├── router.py                   # 로그인, 회원가입 API 엔드포인트
│   │   │   │   ├── schemas.py                  # Pydantic 데이터 검증 모델
│   │   │   │   └── service.py                  # 인증 비즈니스 로직
│   │   │   │
│   │   │   ├── bot/                            # LangGraph 기반 AI 챗봇
│   │   │   │   ├── graph.py                    # 챗봇 상태 머신(State Graph) 및 흐름 제어
│   │   │   │   ├── tools.py                    # 에이전트가 사용하는 도구 (시설 검색, 계산 등)
│   │   │   │   ├── weather.py                  # 기상청 API 연동 모듈
│   │   │   │   └── router.py                  # 챗봇 대화 API
│   │   │   │
│   │   │   ├── feedback/                       # 스포츠맨십 피드백 시스템
│   │   │   │   └── repository.py              # 피드백 데이터 DB 쿼리 처리
│   │   │   │
│   │   │   ├── message/                        # 파티 내 채팅 메시지 관리
│   │   │   │   └── service.py                  # 메시지 전송 및 조회 로직
│   │   │   │
│   │   │   └── party/                          # 파티 매칭 및 라이프사이클 관리
│   │   │       ├── repository.py              # 파티 CRUD 및 필터링 쿼리
│   │   │       ├── router.py                   # 파티 관련 API 엔드포인트
│   │   │       └── service.py                  # 파티 참여, 생성, 마감 등 비즈니스 로직
│   │   │
│   │   ├── config.py                           # 환경 변수 및 공통 설정
│   │   ├── config_auth.py                      # 인증 관련 보안 설정
│   │   ├── db.py                               # DB 연결 및 세션 관리
│   │   └── main.py                             # FastAPI 앱 진입점 및 미들웨어 설정
│   │
│   ├── Dockerfile                              # 서버 배포를 위한 도커 설정
│   ├── docker-compose.yml                      # 컨테이너 오케스트레이션 설정
│   └── requirements.txt                        # Python 의존성 라이브러리 목록
│
├── baro_database/                              # Supabase 스키마, SQL, DB 초기화, ERD 자료
│
├── README.md                                    # 프로젝트 설명 문서
└── .gitignore                                   # Git 제외 파일 설정
```


## 뷰 살펴보기
### app icon
| Splash | App Icon |
|--------|----------|
| <img src="./app images/loading.png" width="230"> | <img src="./app images/logo.jpg" width="230"> |
| 앱 실행 시 처음 보이는 화면 | 앱 아이콘 |

### party view
| Party List | Party Create | Party Detail |
|------------|--------------|--------------|
| <img src="./app images/party.png" width="230"> | <img src="./app images/party create.png" width="230"> | <img src="./app images/party detail.png" width="230"> |
| 파티 목록 화면 | 파티 생성 화면 | 파티 상세 화면 |

### message view
| Message List | Message Chat |
|--------------|--------------|
| <img src="./app images/message.png" width="230"> | <img src="./app images/message chat.png" width="230"> |
| 내가 참여한 파티 목록 | 파티원과 대화하는 채팅 화면 |

### chatbot view
| Bot (Before) | Bot | Bot (After) |
|--------------|------|-------------|
| <img src="./app images/bot before.png" width="230"> | <img src="./app images/bot.png" width="230"> | <img src="./app images/bot after.png" width="230"> |
| 추천 이전 챗봇 화면 | 챗봇 메인 화면 | 운동 추천 결과 화면 |

### login view
| Login | Signup Step 1 | Signup Step 2 |
|--------|----------------|----------------|
| <img src="./app images/login.png" width="230"> | <img src="./app images/signup 1.png" width="230"> | <img src="./app images/signup 2.png" width="230"> |
| 로그인 화면 | 신체 정보 입력 1단계 | 신체 정보 입력 2단계 |

### feedback view
| Feedback | Sportsmanship Rating | Feedback Send |
|----------|------------------------|----------------|
| <img src="./app images/feedback.png" width="230"> | <img src="./app images/feedback sportsmanship.png" width="230"> | <img src="./app images/feedback send.png" width="230"> |
| 피드백 메인 화면 | 스포츠맨십 평가 화면 | 피드백 전송 완료 |

### settings view
| Settings | Profile Edit |
|----------|--------------|
| <img src="./app images/settings.png" width="230"> | <img src="./app images/settings profile.png" width="230"> |
| 설정 메인 | 프로필 수정 화면 |

# 백엔드
Baro의 백엔드 시스템은 **FastAPI**를 기반으로 구축되었으며, 핵심 기능인 **LangGraph 기반의 에이전트형 챗봇**과 **파티 매칭 시스템**을 제공합니다.
단순한 질의응답을 넘어, 챗봇이 스스로 **정보 검색(Search) → 상황 판단(Reason) → 도구 실행(Act)** 의 흐름을 수행하며 사용자에게 최적화된 운동 정보와 코칭을 제공합니다.

#### AI 챗봇 아키텍처 (LangGraph Agent)
챗봇은 사용자의 질문 의도를 파악하고, 날씨, 위치, 신체 정보 등 다양한 맥락(Context)을 고려하여 유기적으로 동작합니다. 시스템은 크게 다음 모듈들로 구성되어 있습니다.

**1. 흐름 제어 및 판단 (Orchestration)**
* **Router**: 앱에서의 모든 요청을 가장 먼저 받는 진입점입니다. 요청 내용을 해석하여 적절한 에이전트나 서비스 로직으로 연결하는 관문 역할을 합니다.
* **LangGraph Agent**: 챗봇의 전체 대화 흐름(State)과 시나리오를 관리합니다. 상황에 따라 다음 단계로 진행할지, 다른 경로로 분기할지를 결정하는 조정자입니다.
* **Supervisor**: 사용자의 요청을 최상위 관점에서 해석하는 컨트롤러입니다. "날씨 확인이 먼저인지", "시설 추천이 필요한지", "다이어트 계산이 필요한지" 등 도구 사용 순서를 결정하고, 여러 도구의 실행 결과를 취합하여 일관된 답변으로 정리합니다.

**2. 인지 및 생성 (Intelligence)**
* **LLM (Brain)**: **GPT-4o-mini** 모델을 사용하여 사용자의 의도(운동 시설 추천, 다이어트 상담, 파티 찾기 등)를 이해합니다. 각 모듈에서 수집한 데이터를 바탕으로 사용자가 이해하기 쉬운 자연스러운 한국어 답변을 생성합니다.

**3. 기능 도구 (Tools)**
Supervisor와 Agent가 상황에 맞춰 호출하는 구체적인 기능 단위입니다.
* **☁️ Weather**: 기상청 API를 실시간으로 호출하여 기온, 강수 여부, 체감 온도 등을 파악합니다. 이를 통해 "오늘 기온은 10도이며 비가 오니 실내 운동을 추천합니다"와 같은 맥락 있는 답변을 제공합니다.
* **💪 Diet Coach**: 사용자의 신체 정보(키, 몸무게, 목표 체중)를 기반으로 BMI와 권장 섭취 칼로리, 목표 달성 예상 기간을 계산합니다. 단순 수치 제공을 넘어 동기를 부여하는 코칭 메시지를 생성합니다.
* **🏢 Recommendations & Scoring**: 사용자 프로필(성별, 선호 종목)과 현재 위치를 기반으로 주변 운동 시설을 조회합니다. 거리, 종목 적합성, 실내/실외 여부를 종합적으로 채점(Scoring)하여 상위 시설을 추천하고, 추천 이유를 함께 제시합니다.
* **🏃 Party List**: 사용자 위치 반경 5km 이내의 모집 중인 운동 파티를 탐색합니다. 거리, 종목, 모집 인원 등으로 필터링하여, 혼자가 아닌 함께 할 운동 파트너를 쉽게 찾을 수 있도록 돕습니다.

# 데이터베이스
#### 1. SONGPA_SPORTS_DATA (송파구 운동 시설)
| 변수명          | 타입    | 상세내용     |
| ------------ | ----- | -------- |
| faci_cd      | text  | 시설 고유 ID |
| faci_nm      | text  | 시설명      |
| faci_addr    | text  | 전체 지번 주소 |
| faci_lat     | float | 위도       |
| faci_lot     | float | 경도       |
| ftype_nm     | text  | 시설 유형    |
| inout_gbn_nm | text  | 실내/실외 구분 |

송파구 내 위치한 체육 시설의 위치 및 속성 정보를 담고 있는 테이블입니다. 사용자의 위치를 기반으로 가까운 시설을 추천하거나, 실내/실외 등 조건에 맞는 장소를 필터링하여 보여줄 때 사용됩니다.
#### 2. SPORTS_PREF (운동 선호도)
| 변수명      | 타입    | 상세내용  |
| -------- | ----- | ----- |
| id       | int   | 고유 ID |
| ages     | text  | 연령대   |
| gender   | text  | 성별    |
| sport_nm | float | 운동 종목 |

연령대 및 성별에 따른 선호 운동 종목 데이터를 관리합니다. 추천 알고리즘에서 사용자 정보(나이, 성별)에 맞는 운동을 가중치 있게 추천하거나, 초기 가입 유저에게 인기 종목을 제안하는 데 활용됩니다.

#### 3. EXERCISE_METHODS (운동 방법)
| 변수명          | 타입   | 상세내용      |
| ------------ | ---- | --------- |
| exer_cd      | int  | 운동 고유 ID  |
| sport_nm     | text | 운동 종목명    |
| equipment_nm | text | 필요 장비     |
| intensity    | text | 강도(상·중·하) |

각 운동 종목별 필요 장비와 운동 강도(상/중/하) 정보를 포함하는 메타 데이터입니다. 챗봇이 사용자에게 구체적인 운동 방법을 설명하거나, 사용자의 컨디션 및 목표 강도에 적합한 운동을 선별하여 추천할 때 참조합니다.

# 팀소개

| 팀원   | 김현   | 서정유 | 최연식  |
|--------|------------------------|-----------------------------|-----------------------------|
| 주소   | [@kimhyun09](https://github.com/kimhyun09) |[@seojeongyoo](https://github.com/seojeongyoo)  | [@yeonsik-choi](https://github.com/yeonsik-choi)|
| 역할   | 안드로이드 전체<br>서버 일부<br>목업 데이터 설계 | DB 설계<br>데이터 정리| 챗봇 개발<br>서버일부        |
