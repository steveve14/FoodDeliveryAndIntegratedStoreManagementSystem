# MVP 실행 계획 — 음식 배달 및 통합 매장 관리 시스템
> 작성일: 2026-02-28  
> 최종 점검일: 2026-02-28  
> 목표: 핵심 E2E 플로우(주문 생성→상태전이→조회) MVP 완성

---

## 전체 진행 요약

| Phase | 진행률 | 비고 |
|---|---|---|
| Phase 0 (버그 수정) | **100%** ✅ | 3건 모두 완료 |
| Phase 1 (백엔드 도메인) | **~85%** | order 상태 전이 규칙·목록 API 미구현 |
| Phase 2 (API 표준화) | **100%** ✅ | 전부 완료 |
| Phase 3 (프론트 연동) | **~5%** | UI 틀만 존재, API 연동 0건 |
| Phase 4 (MVP 검증) | **0%** | 미착수 |

---

## Phase 0 — 즉시 수정 필수 버그: ✅ 완료

1. ~~**AuthGrpcServiceImpl boolean 로직 역전 수정**~~ ✅  
   `validateToken()` 결과가 `false`일 때 `valid: false`, `true`일 때 `valid: true` 정상 로직으로 수정 완료.

2. ~~**Entity ID 타입 전부 통일 (`Long` → `String` UUID)**~~ ✅  
   6개 엔티티(`User`, `Store`, `Order`, `Delivery`, `Event`, `RefreshToken`) 모두 `@Id private String id` + `Persistable<String>` 적용 완료.

3. ~~**AuthService 더미 토큰 제거**~~ ✅  
   `verifyGoogleTokenAndLogin` → `userGrpcClient.getUserByEmail()` + `jwtProvider.createAccessToken()`/`createRefreshToken()` 실제 JWT 발급 로직으로 대체 완료.

---

## Phase 1 — 백엔드 핵심 도메인 구현: ~85%

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

### 1-D. service-order: ⚠️ 70% ← **잔여 작업 집중 필요**
- [x] `CreateOrderRequest`에 `items: List<OrderItemDto>`, `totalAmount` 추가 완료
- [x] `OrderItem` 엔티티 구현 완료 (`id`, `orderId`, `menuId`, `quantity`, `priceSnapshot`)
- [x] 주문 생성 흐름: `StoreGrpcClient.getProductById()` → 매장/메뉴 확인 → 금액 계산 → `orders` + `order_items` 저장
- [~] `PATCH /api/v1/orders/{id}/status` — 엔드포인트 존재하나 **상태 전이 규칙 검증 없음** (무조건 newStatus로 덮어쓰기)  
  **❌ TODO**: `ALLOWED_TRANSITIONS` 맵 추가 (delivery 서비스처럼)
- [ ] **❌ 내 주문 목록 조회 API 미구현** — `GET /api/v1/orders?userId=xxx` + `OrderRepository.findByUserId()` 필요 (단건 조회만 존재)

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

## Phase 3 — 프론트엔드 API 연동: ~5% ← **최대 잔여 작업 영역**

### web-customer: ❌ 미구현 (페이지 1개, API 0건)
- [ ] 로그인/회원가입 페이지 미존재 + `useAuth` composable 없음
- [ ] 매장 목록 — 하드코딩 4개 식당, `useFetch`/`$fetch` 호출 0건
- [ ] 매장 상세 + 메뉴 목록 페이지 미존재
- [ ] 장바구니(Pinia store) 미구현
- [ ] 주문 생성·내 주문 목록·배달 추적 페이지 모두 미존재

### web-shop: ❌ 미구현 (페이지 5개, API 0건)
- [ ] 로그인 — `console.log('Submitted')` + `"// 여기에 실제 로그인 로직 추가하세요"` 주석 상태
- [ ] 주문 접수 화면 (`/orders`) — 네비게이션 링크만 정의, 페이지 파일 없음
- [ ] 메뉴 관리 — 더미 4개 상품 하드코딩, API 연동 없음
- [ ] 재고 관리 — 더미 6개 품목 하드코딩
- [ ] 대시보드 — `Math.random()` 데이터, 배달 관리 페이지 파일 없음

### web-admin: ❌ 미구현 (페이지 15+개, API 0건)
- [ ] 로그인 — `$fetch("/api/auth/login")` 호출하나 서버 라우트 없음 (404 예상)
- [ ] 매장 목록 — `Array.from({ length: 45 })` 더미 45개 잔존
- [ ] 고객 목록 — `useFetch('/api/customers')` → Nuxt 서버 라우트의 mock 데이터
- [ ] 대시보드 — `randomInt()` / `Math.random()` 더미 값
- [ ] 주문/배달 현황 대시보드 페이지 자체 미존재
- [ ] 마케팅/운영/시스템/재무 — 전부 Mock Data (`Array.from()` 패턴)

---

## Phase 4 — MVP 검증 및 정리: ❌ 미착수

- [ ] **E2E 시나리오 수동 테스트**: 고객 주문 생성 → 매장 수락 → 조리 완료 → 배달원 픽업 → 배달 완료 전 구간 검증
- [ ] **환경 변수 정리**: `README_ENV.md`, `env-load.cmd` 기준으로 각 서비스 `application.yml` 하드코딩 값 환경 변수화
- [ ] **핵심 통합 테스트 1건**: Order 생성 → 상태 전이 Spring Boot 통합 테스트 추가

---

## 다음 우선 작업 (MVP 크리티컬 패스)

### 즉시 착수 — 백엔드 잔여 2건
1. **service-order 상태 전이 규칙 추가** — `ALLOWED_TRANSITIONS` 맵 + `updateStatus()` 검증 로직
2. **service-order 주문 목록 API** — `GET /api/v1/orders?userId=xxx` + `OrderRepository.findByUserId()`

### 이후 — 프론트엔드 연동 (MVP 핵심 경로)
3. **web-customer** 로그인 → 매장 목록 → 매장 상세/메뉴 → 장바구니 → 주문
4. **web-shop** 주문 접수 화면 → 상태 업데이트
5. **web-admin** 매장·주문 실데이터 연동

---

## 검증 기준

- [ ] `POST /api/v1/orders` → `order_items` 포함 DB 저장 확인
- [ ] `PATCH /api/v1/orders/{id}/status` 체인 정상 동작 확인
- [ ] `GET /api/v1/deliveries/{id}` 배달 상태 확인
- [ ] web-customer 주문 생성 화면이 실제 백엔드와 통신 확인
- [ ] Gateway 통과 후 `X-User-Id` 헤더가 각 서비스에 정상 전달 확인

---

## 결정 사항

| 항목 | 결정 | 이유 |
|---|---|---|
| Entity ID 타입 | `String` (UUID) | DB 스키마 우선 |
| 서비스 간 통신 | gRPC 유지 | 이미 구현 다수 존재, Feign 전환 비용 高 |
| Spring Boot 버전 | 4.0.2 유지 | 2025-11-20 공식 GA 릴리즈 확인 |
| Kafka/비동기 | MVP 범위 밖 | Phase 4 이후 별도 도입 |
| 모바일(Android) | MVP 범위 밖 | 프론트 연동 완료 후 검토 |