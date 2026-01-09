# 07. 로컬 개발 환경 설정 (Development Setup)

## 1. 필수 요구 사항 (Prerequisites)
*   **Java**: JDK 17 LTS 이상
*   **Node.js**: v18.16.0 이상 (LTS 권장)
*   **IDE**: IntelliJ IDEA (Backend), VS Code (Frontend)
*   **Git**: 최신 버전

## 2. 프로젝트 실행 순서 (Backend)
MSA 특성상 실행 순서가 중요합니다.

1.  **Discovery Service (Eureka)**
    *   Path: `backend/discovery-service`
    *   Command: `./gradlew bootRun`
    *   Check: `http://localhost:8761` 접속
2.  **API Gateway**
    *   Path: `backend/api-gateway`
    *   Command: `./gradlew bootRun`
    *   Port: `8080`
3.  **Core Services (Auth, Store, Order)**
    *   Profile: `-Dspring.profiles.active=local` 옵션 필수.
    *   DB: `local` 프로필 사용 시 프로젝트 내 `sqlite` 파일 자동 생성됨.

## 3. 프로젝트 실행 순서 (Frontend)
1.  **의존성 설치**: `npm install` (각 프로젝트 루트에서)
2.  **개발 서버 실행**: `npm run dev`

## 4. 환경 변수 관리 (.env)
보안 정보(API Key, DB 비밀번호)는 깃에 올리지 않고 `.env` 파일로 관리합니다.
`backend/store-service/src/main/resources/application-secret.yml` 등을 생성하여 `.gitignore`에 등록 후 사용합니다.
