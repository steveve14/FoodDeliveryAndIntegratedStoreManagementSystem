# 11. 우선순위 작업 목록 (실행 백로그)

기준일: 2026-03-04

업데이트: 2026-04-05

## 우선순위 기준

- **P0**: 아키텍처/연동 기준선 확립 (없으면 전체 개발이 흔들리는 항목)
- **P1**: 핵심 사용자 가치 기능 (사장님/고객 주문 플로우)
- **P2**: 확장/운영 고도화 (배포, 관측성, 자동화)

## P0 (완료 및 진행 중)

### ✅ P0-0. 프론트 실연동 차단요소 해소 (완료 — 2026-03-13)

- `GET /api/v1/stores`, `GET /api/v1/stores/*`, `GET /api/v1/stores/*/menus` 비인증 공개
- 그 외 stores 변경/관리 API는 gateway 인증 필터 유지
- web-user E2E 홈 → 카테고리 → 매장 상세 → 장바구니 → 주문 확정까지 통과 확인

### ✅ P0-1. 기준선 확정: 문서↔코드 정렬 정책 (완료 — 2026-03-04)

- 결정: **코드 기준으로 문서를 정렬** (현실을 반영한 문서 최신화)
- 기준 디렉토리: `web-admin`, `web-shop`, `web-user` / `service-*`
- 기술스택 확정: Spring Boot 4.0.2 / Gradle Groovy DSL / H2+PostgreSQL / Spring Data JDBC / Spring gRPC 1.0.2 / pnpm

### ✅ P0-2. API 표준 통일 (완료 — 2026-03-30)

- 목표: `/api/v1` 버저닝 + 공통 응답 모델 + 표준 에러 포맷 전 서비스 적용
- 완료 내역:
  - 공통 `ApiResponse` 모델 전 서비스 일관 적용 (✔)
  - 전 서비스 컨트롤러 응답 형식 통일 (✔)
  - `service-auth` `RestExceptionHandler` `public class` 접근 통일 (✔)
  - 전 서비스 `NoSuchElementException` → 404 핸들러 추가 (✔)
  - 글로벌 예외 처리기(에러 코드 정책 포함) 정비 (✔)

### ✅ P0-3. 서비스 간 통신 표준 적용 (완료 — 2026-03-04)

- **결정**: OpenFeign 폐기 → **Spring gRPC 1.0.2 GA** (Protobuf 스키마 기반) 채택
- **완료 내역**:
  - `net.devh:grpc-spring-boot-starter:3.1.0.RELEASE` 제거
  - `org.springframework.grpc:spring-grpc-spring-boot-starter:1.0.2` 적용
  - Spring gRPC BOM (`spring-grpc-dependencies:1.0.2`) → io.grpc 1.77.1, protobuf 4.33.2 관리
  - 5개 gRPC 서버 `@GrpcService` import 마이그레이션 (`net.devh` → `org.springframework.grpc`)
  - 4개 gRPC 클라이언트: `@GrpcClient` 필드 주입 → `GrpcChannelFactory` 생성자 주입 전환
  - 4개 `GrpcClientConfig.java` 생성 (auth, gateway, order, delivery)
  - 모든 서비스 `application.yml` `grpc.*` → `spring.grpc.*` 접두사 변경
  - **전체 `BUILD SUCCESSFUL`** 확인 (8개 서비스, `clean compileJava`)

### ✅ P0-4. 핵심 도메인 최소 기능(MVP) 정의 및 구현 (완료 — 2026-03-13)

- 주문 생성(`POST /api/v1/orders`), 조회(`GET /api/v1/orders/my`), 상태 전이(`PATCH /api/v1/orders/{id}/status`) 구현 완료
- service-order: gRPC로 service-store 메뉴 검증 후 주문 생성
- Playwright E2E 시나리오 통과: 홈 → 카테고리 → 매장 → 장바구니 담기 → 결제(주문 확정) → 주문 내역 화면 진입
- `run/e2e/order-flow.spec.ts` 14단계 시나리오 `1 passed (4.1s)` 확인 (2026-03-13)

### ✅ P0-5. 하드코딩 고위험 항목 제거 (완료 — 2026-03-10)

- gateway JWT secret `${TOKEN_SECRET}` 환경변수화 완료
- delivery 고정 배달비 `${DELIVERY_DEFAULT_FEE:3000}` 분리 완료
- order VIP 기준 `${ORDER_VIP_THRESHOLD:10}` 분리 완료
- gRPC 주소 `${SERVICE_*_GRPC_ADDR:...}` 환경변수 전환 완료
- Eureka/CORS 주소 `${EUREKA_DEFAULT_ZONE}`, `${GATEWAY_ALLOWED_ORIGIN_*}` 외부화 완료

### ✅ P0-6. 서비스 보안 하드닝 1차 적용 (완료 — 2026-04-05)

- 내부 서비스(`user`, `store`, `order`, `delivery`, `event`)에서 JWT 직접 검증 추가
- `X-User-Id`, `X-User-Role` 헤더와 토큰 클레임 일치 여부 검증 추가
- 주문/배달/이벤트/사용자 프론트 집계 엔드포인트 권한 강화 완료
- Auth 쿠키 `Secure`/`SameSite` 프로파일 분리 적용 완료
- Refresh Token 평문 저장 제거, SHA-256 해시 저장으로 전환 완료
- `docker-compose.yml`에 내부 서비스용 `TOKEN_SECRET` 전달 반영 완료

## P1 (P0 완료 직후)

### P1-1. 사장님 대시보드 핵심 기능

- 통합 주문 목록(배달/매장), 기본 매출 지표, 메뉴 품절 토글
- 완료조건: 운영자 관점 핵심 화면 3종 연동 완료

### P1-2. 매장(태블릿/POS) 핵심 기능

- 테이블 오더, 직원 호출, 테이블 단위 주문 합산
- 완료조건: 매장 내 주문 처리 시나리오 1개 E2E 통과

### P1-3. 고객 웹 주문 플로우 실서비스형 전환

- 현재 정적/템플릿 중심 화면을 API 연동형으로 전환
- 완료조건: 가게검색→장바구니→주문까지 실제 데이터 흐름 연결

### 🟡 P1-4. Android 앱 백엔드 API 연동 (진행 중)

- 기본 네트워크 레이어(Retrofit) 4개 앱 모두 구현
- 주요 화면 API 호출 로직 구현 (현재 실제 호출 코드 작동)
- 배달 앱: `DeliveryDto` 모델 및 `DeliveryController` 엔드포인트 추가
- 키오스크 앱: 하드코딩 Store ID → `KioskConfig` SharedPreferences으로 교체
- 완료조건: E2E 로그인 → 메인 화면 로드 시나리오 동작 (실기기 테스트 필요)

## P2 (안정화/확장)

### 🟡 P2-1. 배포/운영 표준화 (진행 중)

- `Dockerfile` (멀티 스테이지, `ARG SERVICE_NAME` 공용화) 작성 ✔
- `docker-compose.yml` (8개 서비스 + PostgreSQL + Redis) 작성 ✔
- `database/init/00_create_databases.sql` Docker 초기화용 작성 ✔
- K8s 매니페스트 / 환경변수 전략 정비 (미완)
- 완료조건: 스테이징 1회 배포 성공 + 롤백 절차 문서화

### P2-2. 테스트 체계 강화

- 서비스별 API 통합 테스트, 계약 테스트, 핵심 E2E 자동화
- 완료조건: 핵심 플로우 CI 자동검증 구축

### P2-3. 관측성/장애 대응

- 공통 로깅, 트레이싱, 헬스체크, 기본 알람 체계
- 완료조건: 주요 장애 시나리오에 대한 탐지 가능 상태 확보

### P2-4. 보안 후속 하드닝

- `customer-summaries`를 STORE 기준 tenant-scoped 조회로 정교화
- Docker Compose 및 운영 환경에서 내부 서비스 포트 외부 노출 제거
- Gateway 선행 헤더 제거/재주입 정책 명시적 적용
- 운영 환경 gRPC TLS 또는 내부 네트워크 보호 정책 정비
- 완료조건: Gateway 우회 접근과 내부 네트워크 노출을 전제로 한 추가 보안 시나리오 문서화 및 반영

## 실행 순서 권장

1. 사용자 기능 확장 (P1)
2. Android 앱 API 연동 (P1-4)
3. 보안 후속 하드닝 (P2-4)
4. 운영 고도화 (P2)

## 2026-04-05 완료/진행 체크리스트

- [x] 내부 서비스 JWT 직접 검증 추가 (`service-user`, `service-store`, `service-order`, `service-delivery`, `service-event`)
- [x] `X-User-*` 헤더와 JWT 클레임 일치 검증 추가
- [x] `GET /api/v1/orders/{id}` 접근제어 강화
- [x] `GET /api/v1/deliveries/{id}` 접근제어 강화
- [x] `GET /api/v1/orders/frontend/customer-summaries` 역할 제한 추가
- [x] `GET /api/v1/users/me/addresses/**` 역할 제한 추가
- [x] `GET /api/v1/users/frontend/**` ADMIN 제한 추가
- [x] Event API 역할 보호 적용
- [x] Auth 쿠키 `Secure` / `SameSite` 설정 외부화
- [x] Refresh Token 해시 저장 전환
- [x] 변경 서비스 컴파일 검증 성공
- [ ] `customer-summaries` STORE 범위 tenant scoping 구현
- [ ] Docker Compose 내부 서비스 포트 비공개화
- [ ] 운영 환경 gRPC TLS 또는 내부망 보호정책 수립

## 2026-03-30 완료/진행 체크리스트

- [x] P0-2 API 표준 통일: 전 서비스 `RestExceptionHandler` 통일
- [x] `service-auth` `RestExceptionHandler` 접근제한 통일 (package-private → public)
- [x] 전 서비스 `NoSuchElementException` 404 핸들러 추가
- [x] `app-android-delivery` 배달 API 연동 개선 (`DeliveryDto` + 엔드포인트)
- [x] `app-android-kiosk` 하드코딩 스토어 ID 제거 (`KioskConfig` 도입)
- [x] `docker-compose.yml` 작성 (전체 8개 서비스 + PostgreSQL + Redis 통합)
- [x] `Dockerfile` 작성 (멀티 스테이지)
- [x] `database/apply-db-seeds.ps1` DB 시드 자동화 스크립트
- [x] `docs/10_Current_Progress_Status.md` 최신화
- [x] `docs/11_Priority_Worklist.md` 최신화
- [ ] Docker 이미지 빌드 및 실행 테스트 (`docker-compose up`)
- [ ] Android 앱 실기기 API 연동 E2E 실테스트

## 2026-03-17 즉시 실행 체크리스트

- [ ] DB 시드 수동 반영 실행
  - 순서: `07_remote_db_user_seed.sql` → `08_remote_db_store_seed.sql` → `09_remote_db_order_seed.sql` → `10_remote_db_delivery_seed.sql` → `11_remote_db_event_seed.sql`
  - 근거 문서: `docs/2026-03-17-mock-data-inventory-and-db-seed-plan.md`
- [ ] 시드 반영 후 최소 검증 쿼리 실행
  - user/store/order/delivery/event 신규 ID 존재 여부 확인
- [ ] web-shop/web-admin/web-user 실데이터 E2E 스모크 1회 실행
  - 목표: 주문 생성→상태 전이→대시보드 집계 반영 흐름 확인

## 2026-04-06 체크리스트

- [x] `service-event` application.yml `token.secret` 기본값 추가 (기동 시 `BeanCreationException` 해소)
- [x] `service-order`, `service-user`, `service-store`, `service-delivery`, `service-gateway` application.yml `token.secret` 기본값 추가
- [x] 로컬 환경에서 `TOKEN_SECRET` 미설정 시 기동 실패 문제 전 서비스 해소
- [x] 기본값을 auth 서비스 개발용 키와 통일하여 서비스 간 JWT 검증 호환성 확보

---
