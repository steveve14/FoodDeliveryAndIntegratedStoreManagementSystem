# 10. 현재 진행 상황 정리 (2026-03-04)

## 1) 요약
현재 저장소는 **MSA 인프라 골격 + 인증/회원 기능 + 서비스 간 gRPC 통신 + 프론트 템플릿 기반 화면**까지 진행된 상태입니다.

**2026-03-04 마이그레이션 완료**: net.devh gRPC → Spring gRPC 1.0.0-RC1, jjwt 0.11.5 → 0.12.6, 전체 `BUILD SUCCESSFUL` 확인.

## 2) 완료/구축된 항목

### 백엔드 인프라
- 멀티모듈 Gradle 9.2.1 (Groovy DSL) 기반 서비스 구조
  - `service-gateway`, `service-discovery`, `service-auth`, `service-user`, `service-store`, `service-order`, `service-delivery`, `service-event`
- Eureka 기반 서비스 등록/조회 설정
- Spring Cloud Gateway 라우팅 + JWT 필터(`AuthorizationHeaderFilter`) 적용
- **Spring gRPC 1.0.0-RC1** (공식 Spring 프로젝트) 기반 서비스 간 통신 구현
  - gateway→auth (JWT ValidateToken)
  - auth→user (Authenticate / GetUserByEmail)
  - order→store (GetProductById)
  - delivery→order (GetOrderById)
- **jjwt 0.12.6** (Jakarta EE 호환) JWT 생성/검증
- Bean Validation (`@Valid`, `@NotBlank`, etc.) 전 서비스 DTO 적용
- 전역 예외 처리기(`RestExceptionHandler`) 전 서비스 적용
- Spotless + google-java-format 코드 포맷 강제

### 백엔드 비즈니스 기능
- Auth 서비스: 로그인, 소셜 로그인(Google OAuth2), 토큰 갱신, 로그아웃 구현
- User 서비스: 회원 등록/조회 API 구현
- Store/Order/Delivery: 기본 CRUD 구조 및 gRPC 엔드포인트 구현

### 프론트엔드
- Nuxt.js 4.2.2 기반 3개 앱 구조 (`web-admin`, `web-shop`, `web-customer`)
- `web-admin`, `web-shop`: @nuxt/ui 4.3.0 기반 대시보드 템플릿 UI
- `web-customer`: 메인 페이지 중심 초기 UI (Pinia 상태 관리)
- pnpm 10.26.1 워크스페이스 기반 패키지 관리

## 3) 진행 중/초기 상태 항목
- 서비스별 실제 비즈니스 API(주문 상태 전이, 매장/메뉴 재고, POS 정산 등)는 본격 구현 전 단계
- 공통 응답 포맷(`ApiResponse`) 및 API 버저닝(`/api/v1`) 규칙 일부 미적용
- 프론트엔드 백엔드 API 실제 연동 미완성 (템플릿/정적 데이터 단계)

## 4) 현재 기술 스택 (실제 코드 기준)

| 항목 | 실제 사용 기술 |
|---|---|
| Spring Boot | 4.0.2 |
| Build | Gradle 9.2.1 (Groovy DSL) |
| gRPC | Spring gRPC 1.0.0-RC1 / io.grpc 1.76.0 |
| Protobuf | 4.32.1 |
| JWT | jjwt 0.12.6 |
| DB (local) | H2 in-memory |
| DB (prod) | PostgreSQL 5432 |
| ORM | Spring Data JDBC (JPA 미사용) |
| Frontend | Nuxt.js 4.2.2 / @nuxt/ui 4.3.0 |
| 패키지 관리 | pnpm 10.26.1 |

## 5) 리스크 및 남은 항목
- API 규약(`/api/v1`, `ApiResponse<T>`) 미통일 → 프론트-백엔드 연동 비용
- 프론트엔드 백엔드 실연동 미완성
- Dockerfile / docker-compose / K8s 매니페스트 부재
- PostgreSQL 포트 일부 설정 불일치 (5432 vs 6000) — 점검 필요

## 6) 다음 액션(권장)
1. API 표준(버전/응답 포맷/에러 모델) 전 서비스 적용
2. 고객 주문 E2E MVP (주문 생성→상태변경→조회) 구현
3. 프론트엔드 API 실연동 (web-customer 우선)
4. Dockerfile 작성 및 docker-compose로 로컬 통합 실행 환경 구성

---