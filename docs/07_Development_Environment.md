# 07. 로컬 개발 환경 설정 (Development Setup)

> **최종 수정**: 2026-04-05

## 1. 필수 요구 사항 (Prerequisites)

- **Java**: JDK 17 LTS (Eclipse Temurin 권장)
- **Node.js**: v20 LTS 이상
- **pnpm**: v10.26.1 이상 (`npm install -g pnpm`)
- **IDE**: IntelliJ IDEA (Backend), VS Code (Frontend)
- **Git**: 최신 버전

> **주의**: 프론트엔드 패키지 관리자는 **pnpm**을 사용합니다. `npm install`은 사용하지 않습니다.

## 2. 필수 환경 변수

보안 하드닝 이후 로컬 실행 시 아래 값을 우선 확인해야 합니다.

| 변수 | 용도 | 비고 |
| --- | --- | --- |
| `TOKEN_SECRET` | Gateway + 내부 서비스 JWT 검증 | `service-auth`, `service-gateway`, `service-user`, `service-store`, `service-order`, `service-delivery`, `service-event`에 필요 |
| `DB_USER` | PostgreSQL 계정 | 운영/도커 기준 |
| `DB_PASSWORD` | PostgreSQL 비밀번호 | 운영/도커 기준 |
| `GOOGLE_CLIENT_ID` | Google OAuth 설정 | 실제 사용 시 권장 |

> `TOKEN_SECRET`가 누락되면 Gateway뿐 아니라 내부 서비스의 직접 JWT 재검증도 실패할 수 있습니다.

## 3. 프로젝트 실행 순서 (Backend)

MSA 특성상 실행 순서가 중요합니다.

1. **Discovery Service (Eureka)**
   - 경로: `backend/msa-root`
   - 명령어: `./gradlew :service-discovery:bootRun`
   - 확인: `http://localhost:8100`

2. **API Gateway**
   - 경로: `backend/msa-root`
   - 명령어: `./gradlew :service-gateway:bootRun`
   - 포트: `8000`

3. **Core Services (Auth, User, Store, Order, Delivery, Event)**
   - 각 서비스: `./gradlew :service-auth:bootRun` 등
   - DB: `local` 프로파일 사용 시 **H2 in-memory** 자동 실행 (별도 DB 서버 불필요)
   - SQL 스크립트(`schema.sql`, `data.sql`)가 시작 시 자동 실행됩니다.

> **gRPC 포트도 함께 열림**: 각 서비스는 HTTP 포트와 gRPC 포트를 동시에 바인딩합니다. 포트 매핑은 [docs/02_System_Architecture.md](02_System_Architecture.md)를 참고합니다.

## 4. Auth 쿠키 설정 주의사항

- 기본 프로파일에서는 auth 쿠키가 `Secure=true`, `SameSite=Lax`로 설정됩니다.
- 로컬 HTTP 개발 환경에서는 `application-local.yml`에서만 `Secure=false`입니다.
- 로컬에서 로그인/리프레시 테스트가 안 되면 auth 서비스가 `local` 프로파일로 실행 중인지 먼저 확인합니다.

## 5. Docker Compose 사용 시 주의사항

- `docker-compose.yml` 실행 전 `TOKEN_SECRET`를 포함한 환경 변수를 먼저 준비해야 합니다.
- 내부 서비스도 JWT를 직접 검증하므로, auth 서비스만 값이 있어서는 충분하지 않습니다.
- 현재 Compose는 개발 편의상 여러 내부 서비스 포트를 외부에 노출합니다. 운영 환경에서는 그대로 사용하지 않습니다.

## 6. 프로젝트 실행 순서 (Frontend)

1. **의존성 설치**: `pnpm install` (각 프로젝트 루트에서)
2. **개발 서버 실행**: `pnpm dev`

### 각 앱 실행

```bash
# web-admin (port 3000)
cd frontend/web-admin
pnpm install
pnpm dev

# web-shop (port 3100)
cd frontend/web-shop
pnpm install
pnpm dev

# web-user (port 3200)
cd frontend/web-user
pnpm install
pnpm dev
```

### Android 앱

- `frontend/app-android-shop/`, `frontend/app-android-user/`, `frontend/app-android-kiosk/`, `frontend/app-android-delivery/`
- Android Studio에서 열고 빌드/실행 (Gradle 8.1.1, SDK 34)
- 현재 일부 앱은 개발 편의를 위해 cleartext HTTP/하드코딩 Base URL 설정이 남아 있으므로 운영 빌드 정책과 분리 필요

## 7. 환경 변수 관리 (.env)

보안 정보(API Key, DB 비밀번호)는 `.env`, PowerShell 환경 변수, 또는 profile별 secret 파일로 관리합니다.

```yaml
# 예: application-prod.yml (git 제외)
spring:
  datasource:
    url: jdbc:postgresql://db-server:6000/mydb
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

로컬 보조 스크립트:

- `backend/msa-root/env-load.ps1`
- `backend/msa-root/env-load.cmd`

## 8. Spring gRPC 관련 설정 확인

서비스 간 gRPC 통신을 위해 `application.yml`의 아래 설정을 확인합니다.

```yaml
spring:
  grpc:
    server:
      port: 9000
    client:
      channels:
        service-user:
          address: static://localhost:9010
```

- 로컬 개발에서는 plaintext 채널을 사용할 수 있습니다.
- 운영 환경에서는 gRPC TLS 또는 내부망 보호 정책을 별도로 설계해야 합니다.

---
