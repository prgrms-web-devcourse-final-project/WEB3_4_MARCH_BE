# WEB3 Frontend

## 프로젝트 개요
이 프로젝트는 위치 기반 소셜 매칭 서비스의 프론트엔드 애플리케이션입니다. React, TypeScript, Vite를 기반으로 구축되었으며, Stackflow를 사용하여 화면 전환과 라우팅을 관리합니다.

## 기술 스택
- **프레임워크**: React 19
- **언어**: TypeScript
- **빌드 도구**: Vite
- **상태 관리**: Zustand, TanStack React Query
- **스타일링**: Tailwind CSS
- **라우팅/내비게이션**: Stackflow (모바일 앱 스타일 전환)
- **API 통신**: OpenAPI Generator, Fetch API
- **지도 서비스**: Kakao Maps SDK
- **아이콘**: Lucide React
- **날짜 처리**: date-fns
- **유틸리티**: Lodash

## 프로젝트 구조
```
src/
├── activities/          # 앱의 주요 화면 (Stackflow 활동)
├── api/                 # API 클라이언트 및 생성된 API 타입
├── components/          # 재사용 가능한 UI 컴포넌트
├── features/            # 기능별 모음
│   ├── auth/            # 인증 관련 기능
│   ├── chats/           # 채팅 관련 기능
│   ├── keywords/        # 키워드 관련 기능
│   ├── map/             # 지도 관련 기능
│   ├── matching/        # 매칭 관련 기능
│   ├── notifications/   # 알림 관련 기능
│   └── profile/         # 프로필 관련 기능
├── layout/              # 앱 레이아웃 컴포넌트 (상단/하단 바 등)
├── stackflow/           # Stackflow 설정 및 라우팅
├── types/               # 타입 정의
└── utils/               # 유틸리티 함수
```

## 주요 화면
- **LoginActivity**: 로그인 화면
- **ExploreActivity**: 메인 탐색 화면
- **MapActivity**: 지도 화면
- **ChatActivity**: 채팅 화면
- **MyProfileActivity**: 내 프로필 화면
- **ProfileDetailActivity**: 사용자 프로필 상세 화면
- **ProfileSetupActivity**: 프로필 설정 화면
- **NotificationActivity**: 알림 화면

## 개발 환경 설정

### 설치
```bash
# yarn 사용
yarn install
```

### 개발 서버 실행
```bash
# yarn 사용
yarn dev
```

### 빌드
```bash
# yarn 사용
yarn build
```

### API 코드 생성
```bash
# yarn 사용
yarn codegen
```

## 환경 변수
`.env` 파일에 다음과 같은 환경 변수를 설정해야 합니다:
```
VITE_DEFAULT_KAKAO_API_KEY=<카카오 API 키>
VITE_DEFAULT_KAKAO_MAP_API_KEY=<카카오 맵 API 키>
VITE_DEFAULT_SERVER_URL=<서버 URL>
```

- `VITE_DEFAULT_KAKAO_API_KEY`: 카카오 로그인 및 API 사용을 위한 키
- `VITE_DEFAULT_KAKAO_MAP_API_KEY`: 카카오 맵 API 사용을 위한 키
- `VITE_DEFAULT_SERVER_URL`: 백엔드 서버 URL (기본값: https://api.connect-to.shop)

## 추가 정보

### OpenAPI 코드 생성
이 프로젝트는 OpenAPI Generator를 사용하여 백엔드 API와 통신하는 코드를 자동으로 생성합니다. API 스펙이 변경되면 다음 명령어를 실행하여 코드를 업데이트할 수 있습니다:
```bash
yarn codegen
```

**사용법**
- `apiClient.` .클릭시 intelisence로 서버의 서비스 리스트가 나오고 이를 통해 API를 호출할 수 있어요.

### 모바일 환경 최적화
이 애플리케이션은 모바일 환경에 최적화되어 있으며, Stackflow를 사용하여 네이티브 모바일 앱과 유사한 화면 전환 경험을 제공합니다.

### 배포
```bash
# 프로덕션 빌드 생성
yarn build

# 빌드 미리보기
yarn preview
```

