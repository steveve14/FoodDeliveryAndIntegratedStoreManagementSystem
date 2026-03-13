# 11. 우선순위 작업 목록 (실행 백로그)

기준일: 2026-03-04

업데이트: 2026-03-10

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
- 기술스택 확정: Spring Boot 4.0.2 / Gradle Groovy DSL / H2+PostgreSQL / Spring Data JDBC / Spring gRPC 1.0.0-RC1 / pnpm

### ⬜ P0-2. API 표준 통일 (진행 필요)
- 목표: `/api/v1` 버저닝 + 공통 응답 모델 + 표준 에러 포맷 전 서비스 적용
- 작업:
  - 공통 `ApiResponse` 모델 전 서비스 일관 적용
  - 전 서비스 컨트롤러 응답 형식 통일
  - 글로벌 예외 처리기(에러 코드 정책 포함) 정비
- 완료조건: Auth/User/Store/Order 서비스의 신규·기존 공개 API 100% 표준 준수

### ✅ P0-5. 하드코딩 고위험 항목 제거 (완료 — 2026-03-10)
- gateway JWT secret `${TOKEN_SECRET}` 환경변수화 완료
- delivery 고정 배달비 `${DELIVERY_DEFAULT_FEE:3000}` 분리 완료
- order VIP 기준 `${ORDER_VIP_THRESHOLD:10}` 분리 완료
- gRPC 주소 `${SERVICE_*_GRPC_ADDR:...}` 환경변수 전환 완료
- Eureka/CORS 주소 `${EUREKA_DEFAULT_ZONE}`, `${GATEWAY_ALLOWED_ORIGIN_*}` 외부화 완료

### ✅ P0-3. 서비스 간 통신 표준 적용 (완료 — 2026-03-04)
- **결정**: OpenFeign 폐기 → **Spring gRPC 1.0.0-RC1** (Protobuf 스키마 기반) 채택
- **완료 내역**:
  - `net.devh:grpc-spring-boot-starter:3.1.0.RELEASE` 제거
  - `org.springframework.grpc:spring-grpc-spring-boot-starter:1.0.0-RC1` 적용
  - Spring gRPC BOM (`spring-grpc-dependencies:1.0.0-RC1`) → io.grpc 1.76.0, protobuf 4.32.1 관리
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

## P2 (안정화/확장)

### P2-1. 배포/운영 표준화
- Dockerfile / K8s 매니페스트 / 환경변수 전략 정비
- 완료조건: 스테이징 1회 배포 성공 + 롤백 절차 문서화

### P2-2. 테스트 체계 강화
- 서비스별 API 통합 테스트, 계약 테스트, 핵심 E2E 자동화
- 완료조건: 핵심 플로우 CI 자동검증 구축

### P2-3. 관측성/장애 대응
- 공통 로깅, 트레이싱, 헬스체크, 기본 알람 체계
- 완료조건: 주요 장애 시나리오에 대한 탐지 가능 상태 확보

## 실행 순서 권장
1. API 표준 고정 (P0-2)
2. MVP 플로우 구현 (P0-4)
3. 사용자 기능 확장 (P1)
4. 운영 고도화 (P2)

---