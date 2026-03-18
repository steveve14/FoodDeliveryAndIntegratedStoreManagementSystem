# 10. 현재 진행 상황 정리 (2026-03-17)

## 0) 2026-03-10 추가 반영 (금일)

- `service-store` 카테고리 처리 로직을 코드 정규화 기반으로 확장
  - 입력값 한글/영문 혼용(`치킨`, `CHICKEN`) 모두 표준 코드로 변환
  - 카테고리 프로필(ETA/아이콘/태그) 확장: `SNACK`, `NIGHT`, `BOSSAM`, `ASIAN`, `SALAD`, `LUNCHBOX` 포함
- `web-user` 사용자 카테고리를 음식앱 기준으로 확장
  - 기존: 치킨/한식/분식/일식/디저트
  - 확장: 중식/양식/피자/버거/족발보쌈/야식/아시안/샐러드/도시락/카페 추가
  - 백엔드 카테고리 코드(`KOREAN`, `CHICKEN` 등)를 사용자 라벨로 매핑해 필터 일관성 확보
- 백엔드-프론트 연동 점검
  - `service-store` 컴파일 성공 (`:service-store:compileJava`)
  - `web-user` 서버 기동 확인 (`http://localhost:3010` → 200)
  - `/api/v1/stores` 인증 정책 이슈 식별(당시 401)
- 하드코딩 전수 점검(우선순위 중심) 수행
  - 보안/운영성 영향 하드코딩 항목 식별 (JWT secret, 고정 수수료, 고정 VIP 기준, 정적 gRPC 주소 등)

## 0-1) 2026-03-13 추가 반영

- **P0-0 완료**: `GET /api/v1/stores`, `GET /api/v1/stores/*`, `GET /api/v1/stores/*/menus` 비인증 공개 확정
  - gateway `service-store-public` route로 인증 없이 접근 가능
- **SSR 쿠키 삭제 버그 수정** ([useApi.ts](../frontend/web-user/app/composables/useApi.ts))
  - SSR 중 401 응답 시 서버에서 `user-session` 쿠키를 삭제하던 문제 수정
  - `userSession.value = null` 을 `if (import.meta.client)` 가드로 보호
- **gateway JWT secret 정렬**
  - auth 서비스 HS384 키와 gateway `TOKEN_SECRET` 일치 필요
  - gateway 재기동 시 동일 Base64 secret 주입 필요
- **P0-4 완료**: Playwright E2E 14단계 시나리오 통과
  - 홈 진입 → 카테고리 필터 → 매장 상세 → 장바구니 담기 → 결제(주문 확정) → 주문 내역 화면 진입
  - 장바구니 비어있을 경우 API 직접 추가 fallback 포함
  - 결과: `1 passed (4.1s)`
- **gateway CORS 개선**: `allowedOrigins`에 개발 서버 포트 3010, 3100, 3200 추가

## 0-2) 2026-03-15 추가 반영

- **Spring gRPC GA 업그레이드**: 1.0.0-RC1 → **1.0.2 GA** (Maven Central 정식 배포)
  - io.grpc: 1.76.0 → **1.77.1**, Protobuf: 4.32.1 → **4.33.2**
  - Spring Boot 4.0.2 정식 지원
- **Android 앱 스캐폴드 추가**: 4개 네이티브 앱 프로젝트 구축
  - `frontend/app-android-shop/` (매장용, `com.fooddelivery.shop`)
  - `frontend/app-android-user/` (고객용, `com.fooddelivery.user`)
  - `frontend/app-android-kiosk/` (키오스크용, `com.fooddelivery.kiosk`)
  - `frontend/app-android-delivery/` (라이더/배달용, `com.fooddelivery.delivery`)
  - Android SDK 34, minSdk 24, Material Design, ConstraintLayout
  - 기본 MainActivity 스캐폴드 완료, API 연동 추후 진행
- **전체 문서 최신화**: docs/ 전체 문서를 실제 코드 기준으로 정렬

## 0-3) 2026-03-17 추가 반영

- **프론트 주요 mock 제거 완료**
  - `web-admin`: 운영/마케팅/시스템 주요 화면 mock 제거 + `system/versions.vue` 랜덤 데이터 제거
  - `web-shop`: `layouts/default.vue` 배달 대기 주문 더미 제거 (`useHomeStoreSource` 기반)
  - `web-user`: `HomeSales`, `HomeStats`, `HomeChart.client` 랜덤/더미 제거 (`useHomeUserSource` 신설)
- **DB 시드 확장 완료 (유형별 5건)**
  - `backend/msa-root/database/07~11_remote_db_*_seed.sql`에 user/store/order/delivery/event 각 5건 추가
  - 재실행 안정성 위해 `DELETE ... IN (...)` 대상 확장
- **작업 근거 문서 추가**
  - `docs/2026-03-17-mock-data-inventory-and-db-seed-plan.md` 작성 및 최신화
  - TODO 반영: `docs/2026-03-13-todo-list.md` 최신 상태로 갱신
  - MVP 계획 문서 링크 반영: `docs/2026-02-28/MVP_실행계획.md`

## 1) 요약

현재 저장소는 **MSA 인프라 골격 + 인증/회원 기능 + 서비스 간 gRPC 통신 + 프론트 실연동 확장 단계**까지 진행되었습니다.

**2026-03-04 마이그레이션 완료**: net.devh gRPC → Spring gRPC 1.0.2 GA, jjwt 0.11.5 → 0.12.6, 전체 `BUILD SUCCESSFUL` 확인.

## 2) 완료/구축된 항목

### 백엔드 인프라

- 멀티모듈 Gradle 9.2.1 (Groovy DSL) 기반 서비스 구조
  - `service-gateway`, `service-discovery`, `service-auth`, `service-user`, `service-store`, `service-order`, `service-delivery`, `service-event`
- Eureka 기반 서비스 등록/조회 설정
- Spring Cloud Gateway 라우팅 + JWT 필터(`AuthorizationHeaderFilter`) 적용
- Gateway 죽은 경로 분기 제거 완료
  - `/api/v1/admin/users/**`, `/api/v1/stores/manage/**` 제거
  - 실제 공개/보호 경로만 유지
  - 세부 권한은 각 서비스의 `@RequireRole` 및 소유권 검증으로 위임
- **Spring gRPC 1.0.2 GA** 기반 서비스 간 통신 구현
  - gateway→auth (JWT ValidateToken)
  - auth→user (Authenticate / GetUserByEmail)
  - order→store (GetProductById)
  - delivery→order (GetOrderById)
- **jjwt 0.12.6** JWT 생성/검증
- Bean Validation (`@Valid`, `@NotBlank`, etc.) 전 서비스 DTO 적용
- 전역 예외 처리기(`RestExceptionHandler`) 전 서비스 적용
- Spotless + google-java-format 코드 포맷 강제

### 백엔드 비즈니스 기능

- Auth 서비스: 로그인, 소셜 로그인(Google OAuth2), 토큰 갱신, 로그아웃 구현
- 인증 상태는 httpOnly 쿠키(access-token, refresh-token) 중심으로 운영
- User 서비스: 회원 등록/조회 API 구현
- Store/Order/Delivery: 기본 CRUD 구조 및 gRPC 엔드포인트 구현
- Order MVP 경로 구현
  - 주문 생성 (`POST /api/v1/orders`)
  - 주문 조회 (`GET /api/v1/orders/my`, `GET /api/v1/orders/{id}`)
  - 주문 상태 전이 (`PATCH /api/v1/orders/{id}/status`)

### 프론트엔드

- Nuxt.js 4.2.2 기반 3개 앱 구조 (`web-admin`, `web-shop`, `web-user`)
- `web-admin`, `web-shop`: @nuxt/ui 4.3.0 기반 대시보드 템플릿 UI
- `web-user`: 카테고리/매장/장바구니/체크아웃/주문내역 연동 경로 확보
- pnpm 10.26.1 워크스페이스 기반 패키지 관리

### Android 앱

- 4개 네이티브 앱 스캐폴드 구축 완료
  - `app-android-shop` (매장용, com.fooddelivery.shop)
  - `app-android-user` (고객용, com.fooddelivery.user)
  - `app-android-kiosk` (키오스크용, com.fooddelivery.kiosk)
  - `app-android-delivery` (라이더/배달용, com.fooddelivery.delivery)
- Android SDK 34, minSdk 24, Material Design 1.10.0
- 기본 MainActivity 구조 완성, 백엔드 API 연동 예정

## 3) 진행 중 항목

- 공통 응답 포맷(`ApiResponse`) 및 API 버저닝(`/api/v1`) 규칙 전 서비스 100% 일관 적용은 추가 정리 필요
- web-shop, web-admin, web-user 실데이터 화면 기준 E2E 스모크 확장 필요
- DB 시드 수동 반영(07→11 순차 실행) 및 반영 후 검증 로그 정리 필요

## 4) 현재 기술 스택 (실제 코드 기준)

| 항목 | 실제 사용 기술 |
| --- | --- |
| Spring Boot | 4.0.2 |
| Build | Gradle 9.2.1 (Groovy DSL) |
| gRPC | Spring gRPC 1.0.2 GA / io.grpc 1.77.1 |
| Protobuf | 4.33.2 |
| JWT | jjwt 0.12.6 |
| DB (local) | H2 in-memory |
| DB (prod) | PostgreSQL 6000 |
| ORM | Spring Data JDBC (JPA 미사용) |
| Frontend | Nuxt.js 4.2.2 / @nuxt/ui 4.3.0 |
| 패키지 관리 | pnpm 10.26.1 |

## 5) 리스크 및 남은 항목

- API 문서와 구현 간 일부 불일치 잔존
- Dockerfile / docker-compose / K8s 매니페스트 부재
- gateway 재기동 시 `TOKEN_SECRET` 누락 위험 (운영 스크립트 보강 필요)
- Android 앱 API 연동 미구현 (스캐폴드만 완료)

## 6) 다음 액션(권장)

1. API 표준(버전/응답 포맷/에러 모델) 전 서비스 적용
2. DB 시드 수동 반영 실행 (`07` → `08` → `09` → `10` → `11`) 및 결과 확인
3. web-shop/web-admin/web-user E2E 스모크 추가
4. 서비스 기동 순서 + 환경변수 포함 실행 스크립트 정리
5. Dockerfile 작성 및 docker-compose 로컬 통합 실행 환경 구성
6. Android 앱 백엔드 API 연동 (app-android-shop/user/kiosk/delivery)
