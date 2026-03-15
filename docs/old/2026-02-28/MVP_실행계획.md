# MVP 실행 계획 — 음식 배달 및 통합 매장 관리 시스템
> 작성일: 2026-02-28  
> 최종 점검일: 2026-03-13  
> 목표: 핵심 E2E 플로우(주문 생성→상태전이→조회) MVP 완성

---

## 전체 진행 요약

| Phase | 진행률 | 비고 |
| --- | --- | --- |
| Phase 0 (버그 수정) | **100%** ✅ | 3건 모두 완료 |
| Phase 1 (백엔드 도메인) | **~95%** | MVP 핵심 API 완료, 역할 모델 정교화 잔여 |
| Phase 2 (API 표준화) | **100%** ✅ | 전부 완료 |
| Phase 3 (프론트 연동) | **~40%** | web-user 핵심 주문 플로우 연동 완료, web-shop/web-admin 잔여 |
| Phase 4 (MVP 검증) | **~35%** | web-user 주문 E2E 1건 통과, 교차앱/운영검증 미완 |

---

## Phase 0 — 즉시 수정 필수 버그: ✅ 완료

1. ~~**AuthGrpcServiceImpl boolean 로직 역전 수정**~~ ✅  
   `validateToken()` 결과가 `false`일 때 `valid: false`, `true`일 때 `valid: true` 정상 로직으로 수정 완료.

2. ~~**Entity ID 타입 전부 통일 (`Long` → `String` UUID)**~~ ✅  
   6개 엔티티(`User`, `Store`, `Order`, `Delivery`, `Event`, `RefreshToken`) 모두 `@Id private String id` + `Persistable<String>` 적용 완료.

3. ~~**AuthService 더미 토큰 제거**~~ ✅  
   `verifyGoogleTokenAndLogin` → `userGrpcClient.getUserByEmail()` + `jwtProvider.createAccessToken()`/`createRefreshToken()` 실제 JWT 발급 로직으로 대체 완료.

---

## Phase 1 — 백엔드 핵심 도메인 구현: ~95%

### 1-A. service-auth: ✅ 완료
- [x] `UserGrpcClient.authenticate(email, password)` → 비밀번호 검증 → JWT 발급 흐름 완성
- [x] `POST /api/v1/auth/refresh` — DB 토큰 조회 → 유효성 검증 → 새 토큰 발급 + rotation
- [x] `POST /api/v1/auth/logout` — refresh token revoke + DB 삭제

### 1-B. service-user: ⚠️ 95%
- [~] `User` 엔티티: `roles`(String), `provider`, `providerId` 필드 존재 ✅  
  **⚠️ 잔여**: `roles`가 String 타입이고 등록 시 항상 `"USER"`로 세팅. CUSTOMER/OWNER/RIDER/ADMIN enum화 미완
- [x] 주소 CRUD API (`/api/v1/users/me/addresses`) — `Address` 엔티티/Repository/Service/Controller 전체 구현 완료
- [x] 회원 탈퇴 `DELETE /api/v1/users/{id}` → `userService.withdraw(id)` 구현 완료

### 1-C. service-store: ✅ 완료
- [x] `Store` 엔티티 14필드 확장 (`category`, `status`, `latitude`, `longitude`, `minOrderAmount`, `ratingAvg`, `description`, `openingHours`, `ownerId`, `createdAt` 등)
- [x] Menu 엔티티/Repository/Service/Controller 전체 구현 완료
- [x] 메뉴 CRUD: `GET/POST/PUT/DELETE /api/v1/stores/{storeId}/menus`
- [x] 매장 목록 조회: `GET /api/v1/stores?category=&status=` (카테고리/상태 필터)
- [x] `StoreGrpcServiceImpl.getStoreById()` — 전체 필드 매핑 반환 완성

### 1-D. service-order: ✅ 95% (MVP 기준 핵심 완료)
- [x] `CreateOrderRequest`에 `items: List<OrderItemDto>`, `totalAmount` 추가 완료
- [x] `OrderItem` 엔티티 구현 완료 (`id`, `orderId`, `menuId`, `quantity`, `priceSnapshot`)
- [x] 주문 생성 흐름: `StoreGrpcClient.getProductById()` → 매장/메뉴 확인 → 금액 계산 → `orders` + `order_items` 저장
- [x] `PATCH /api/v1/orders/{id}/status` 상태 전이 경로 구현 및 실사용 경로 연동
- [x] 내 주문 목록 조회 API 구현 (`GET /api/v1/orders/my`)
- [x] 점주 주문 목록 조회 API 구현 (`GET /api/v1/orders/store/{storeId}`)

### 1-E. service-delivery: ✅ 완료
- [x] `OrderGrpcClient.getOrderById()` → 주문 존재 확인 후 배달 생성
- [x] `PATCH /api/v1/deliveries/{id}/status` + `ALLOWED_TRANSITIONS` 맵으로 전이 규칙 검증
  - `SCHEDULED → PICKED_UP → COMPLETED`
- [x] `deliveryFee` = 고정 3,000원 (MVP 수준 수용)

---

## Phase 2 — API 표준화: ✅ 완료

- [x] **URI 통일**: 8개 서비스 전체 `/api/v1/` 접두사 일관 적용
- [x] **`ApiResponse<T>` 래퍼**: 8개 서비스 모두 `ApiResponse.ok()` / `ApiResponse.error()` 사용
  - ⚠️ 참고: 각 서비스에 동일 클래스 중복 존재 → 향후 shared-common 모듈 추출 고려
- [x] **글로벌 예외 처리**: 8개 서비스 모두 `RestExceptionHandler` + `@RestControllerAdvice` 적용

---

## Phase 3 — 프론트엔드 API 연동: ~40%

### web-user: ✅ MVP 핵심 플로우 연동 완료
- [x] 로그인/회원가입 및 쿠키 세션 기반 인증 플로우 연결
- [x] 매장 목록/카테고리/매장 상세/메뉴 API 연동
- [x] 장바구니-체크아웃-주문 확정-주문내역 화면까지 연동
- [x] 주문 E2E 시나리오 1건 통과 (`1 passed (4.1s)`, 2026-03-13)

### web-shop: ⚠️ 부분 진행
- [x] 주문/인증 일부 API 연동 기반 확보
- [x] E2E 스모크 기반(홈 진입) 구성
- [ ] 주문 접수/재고/대시보드의 실데이터 연결 확장 필요

### web-admin: ⚠️ 초기 정리 진행
- [x] E2E 스모크 기반(홈 진입) 구성
- [ ] 운영/마케팅/재무/시스템 영역 실데이터 연동 필요
- [ ] mock 기반 목록/대시보드 데이터 제거 필요

---

## Phase 4 — MVP 검증 및 정리: ~35%

- [x] **web-user E2E 자동 검증 1건**: 주문 생성→주문내역 표시까지 완료
- [x] **고위험 하드코딩 환경변수화**: JWT secret, gRPC 주소, Eureka/CORS, 고정 수수료/기준값 반영
- [ ] **서비스 간 상태전이 통합 테스트 강화**: Order↔Delivery 연계 구간 보강 필요
- [ ] **교차앱 E2E 확장**: web-shop/web-admin 실데이터 시나리오 추가 필요

---

## 다음 우선 작업 (MVP 크리티컬 패스)

### 즉시 착수 — P0 잔여 1건
1. **API 표준 100% 정렬(P0-2)** — 응답 포맷/에러 모델/버저닝 일관성 최종 점검

### 이후 — MVP 확장 경로
2. **web-shop 실연동 확장** — 주문 접수/재고/대시보드 실데이터 전환
3. **web-admin 실연동 확장** — 운영/마케팅/재무/시스템 mock 제거
4. **통합 실행 안정화** — 서비스 기동 스크립트/환경변수 주입 운영 절차 고정

---

## 검증 기준

- [x] `POST /api/v1/orders` → `order_items` 포함 저장 및 화면 반영 확인
- [x] `PATCH /api/v1/orders/{id}/status` MVP 경로 동작 확인
- [x] web-user 주문 생성 화면 ↔ 백엔드 통신 확인
- [x] Gateway 경유 인증 헤더 전달 기반 동작 확인
- [ ] `GET /api/v1/deliveries/{id}` 포함한 주문-배달 연쇄 E2E 추가 검증
- [ ] web-shop/web-admin 실데이터 기반 E2E 시나리오 추가 검증

---

## 결정 사항

| 항목 | 결정 | 이유 |
| --- | --- | --- |
| Entity ID 타입 | `String` (UUID) | DB 스키마 우선 |
| 서비스 간 통신 | gRPC 유지 | 이미 구현 다수 존재, Feign 전환 비용 高 |
| Spring Boot 버전 | 4.0.2 유지 | 2025-11-20 공식 GA 릴리즈 확인 |
| Kafka/비동기 | MVP 범위 밖 | Phase 4 이후 별도 도입 |
| 모바일(Android) | MVP 범위 밖 | 프론트 연동 완료 후 검토 |