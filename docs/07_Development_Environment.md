# 07. 로컬 개발 환경 설정 (Development Setup)

> **최종 수정**: 2026-03-04

## 1. 필수 요구 사항 (Prerequisites)
*   **Java**: JDK 17 LTS (Eclipse Temurin 권장)
*   **Node.js**: v20 LTS 이상
*   **pnpm**: v10.26.1 이상 (`npm install -g pnpm`)
*   **IDE**: IntelliJ IDEA (Backend), VS Code (Frontend)
*   **Git**: 최신 버전

> **주의**: 프론트엔드 패키지 관리자는 **pnpm**을 사용합니다. `npm install`은 사용하지 않습니다.

## 2. 프로젝트 실행 순서 (Backend)
MSA 특성상 실행 순서가 중요합니다.

1.  **Discovery Service (Eureka)**
    *   경로: `backend/msa-root`
    *   명령어: `./gradlew :service-discovery:bootRun`
    *   확인: `http://localhost:8100` 접속

2.  **API Gateway**
    *   경로: `backend/msa-root`
    *   명령어: `./gradlew :service-gateway:bootRun`
    *   포트: `8000`

3.  **Core Services (Auth, User, Store, Order, Delivery, Event)**
    *   각 서비스: `./gradlew :service-auth:bootRun` 등
    *   DB: `local` 프로파일 사용 시 **H2 in-memory** 자동 실행 (별도 DB 서버 불필요)
    *   SQL 스크립트(`schema.sql`, `data.sql`)가 시작 시 자동 실행됩니다.

> **gRPC 포트도 함께 열림**: 각 서비스는 HTTP 포트와 gRPC 포트를 동시에 바인딩합니다. (포트 매핑은 `02_System_Architecture.md` 참고)

## 3. 프로젝트 실행 순서 (Frontend)
1.  **의존성 설치**: `pnpm install` (각 프로젝트 루트에서)
2.  **개발 서버 실행**: `pnpm dev`

### 각 앱 실행
```bash
# web-admin
cd frontend/web-admin
pnpm install
pnpm dev

# web-shop
cd frontend/web-shop
pnpm install
pnpm dev

# web-user
cd frontend/web-user
pnpm install
pnpm dev
```

## 4. 환경 변수 관리 (.env)
보안 정보(API Key, DB 비밀번호)는 깃에 올리지 않고 `.env` 파일로 관리합니다.  
`application-secret.yml` 또는 환경변수 파일을 서비스별 `src/main/resources/`에 생성 후 `.gitignore`에 등록하여 사용합니다.

```yaml
# 예: application-prod.yml (git 제외)
spring:
  datasource:
    url: jdbc:postgresql://db-server:6000/mydb
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

## 5. Spring gRPC 관련 설정 확인
서비스 간 gRPC 통신을 위해 `application.yml`의 아래 설정을 확인하세요:

```yaml
spring:
  grpc:
    server:
      port: 9000       # 서비스별 gRPC 서버 포트
    client:
      channels:
        service-user:
          address: static://localhost:9010  # 상대 서비스 gRPC 주소
```

> Spring Milestones 저장소(`https://repo.spring.io/milestone`)가 `build.gradle`에 등록되어 있어야 합니다. (Spring gRPC 1.0.0-RC1 의존성)

---