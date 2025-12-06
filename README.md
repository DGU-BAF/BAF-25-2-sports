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
    -

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
├── baro_backend/                               # FastAPI 기반 서버 (파티, 메시지, 챗봇, 인증 API)
│                                                # Docker, LangGraph, Supabase 연동 포함
│
├── baro_database/                              # Supabase 스키마, SQL, DB 초기화, ERD 자료
│
├── README.md                                    # 프로젝트 설명 문서
└── .gitignore                                   # Git 제외 파일 설정
```


## 뷰 살펴보기
### app icon
### party view
### message view
### chatbot view
### login view
### other view

# 팀소개
