# 🔗 Web-Admin ↔ Backend 연동 가이드

> **작성일**: 2026-03-02  
> **대상**: 프론트엔드 개발자 (Nuxt 4 + Nuxt UI)

---

## 📌 목차

1. [아키텍처 흐름](#1-아키텍처-흐름)
2. [환경 설정](#2-환경-설정)
3. [API 클라이언트 구조](#3-api-클라이언트-구조)
4. [인증 흐름](#4-인증-흐름)
5. [도메인별 API 서비스](#5-도메인별-api-서비스)
6. [에러 처리](#6-에러-처리)
7. [페이지에서 사용하기](#7-페이지에서-사용하기)
8. [체크리스트](#8-체크리스트)

---

## 1. 아키텍처 흐름

```
[브라우저]  →  [Nuxt Dev Server (3000)]  →  [Spring Cloud Gateway (8000)]  →  [각 MSA 서비스]
           ↑                              ↑
    Nitro 프록시 (/api/v1/**)       JWT 검증 + X-User-Id 자동 주입
```

### 핵심 원칙

| 항목 | 설명 |
|------|------|
| **프록시** | Nuxt Nitro가 `/api/v1/**` 요청을 `http://localhost:8000/api/v1/**`로 프록시 |
| **인증** | 모든 인증 API 요청에 `Authorization: Bearer {accessToken}` 헤더 첨부 |
| **토큰 갱신** | 401 응답 시 자동으로 `refreshToken`으로 갱신 후 원래 요청 재시도 |
| **역할 기반 접근** | `auth.global.ts` 미들웨어에서 `ADMIN`, `STORE`, `USER` 권한 분기 |

### Gateway 라우팅

| 경로 패턴 | 대상 서비스 | 포트 |
|-----------|------------|------|
| `/api/v1/auth/**` | service-auth | 7000 |
| `/api/v1/users/**` | service-user | 8010 |
| `/api/v1/stores/**` | service-store | 8020 |
| `/api/v1/events/**` | service-event | 8030 |
| `/api/v1/orders/**` | service-order | 8040 |
| `/api/v1/deliveries/**` | service-delivery | 8050 |

---

## 2. 환경 설정

### 2-1. `.env` 파일

```env
# Backend Gateway 주소 (기본값: http://localhost:8000)
NUXT_PUBLIC_API_BASE_URL=http://localhost:8000
```

### 2-2. `nuxt.config.ts` 프록시 설정 (이미 적용됨)

```typescript
nitro: {
  routeRules: {
    '/api/v1/**': { proxy: 'http://localhost:8000/api/v1/**' }
  }
}
```

### 2-3. 런타임 설정 (`nuxt.config.ts`에 추가)

```typescript
runtimeConfig: {
  public: {
    apiBaseUrl: 'http://localhost:8000'
  }
}
```

---

## 3. API 클라이언트 구조

### 파일 구조

```
app/
├── composables/
│   ├── useApi.ts            ← 핵심 API 클라이언트 ($api 래퍼)
│   ├── useAuth.ts           ← 인증 (login/logout/refresh)
│   └── api/
│       ├── useUserApi.ts    ← 회원 도메인 API
│       ├── useStoreApi.ts   ← 가게 도메인 API
│       ├── useMenuApi.ts    ← 메뉴 도메인 API
│       ├── useOrderApi.ts   ← 주문 도메인 API
│       ├── useDeliveryApi.ts ← 배달 도메인 API
│       └── useEventApi.ts   ← 이벤트 도메인 API
├── types/
│   └── api.ts               ← Backend DTO 타입 정의
├── plugins/
│   └── auth-refresh.client.ts ← 앱 시작 시 토큰 복원
└── middleware/
    └── auth.global.ts        ← 라우트 보호
```

### 호출 계층

```
페이지/컴포넌트
    ↓ 호출
도메인 API 컴포저블 (useStoreApi, useOrderApi ...)
    ↓ 내부적으로 사용
useApi.$api()  ← Authorization 헤더 자동 첨부 + 401 자동 갱신
    ↓
$fetch → Nitro Proxy → Gateway → 각 서비스
```

---

## 4. 인증 흐름

### 4-1. 로그인

```typescript
const { login } = useAuth()
await login('admin@example.com', 'password')
// → POST /api/v1/auth/login
// → accessToken, refreshToken 을 쿠키에 저장
// → JWT 디코딩하여 user 세션 저장
```

### 4-2. 토큰 자동 갱신

`useApi`의 `$api()` 함수가 **401 응답**을 받으면:
1. `refreshToken`으로 `POST /api/v1/auth/refresh` 호출
2. 새 토큰 저장
3. 실패했던 원래 요청을 새 토큰으로 **자동 재시도**

```
요청 → 401 → refresh 시도 → 성공 → 원래 요청 재시도
                          → 실패 → 로그아웃 + /login 이동
```

### 4-3. 로그아웃

```typescript
const { logout } = useAuth()
await logout()
// → POST /api/v1/auth/logout  (refreshToken 서버 무효화)
// → 쿠키 삭제 + /login 이동
```

---

## 5. 도메인별 API 서비스

### 사용 예시

```vue
<script setup lang="ts">
// 가게 API 사용
const { getStores, getStore, createStore } = useStoreApi()

// 가게 목록 조회
const { data: stores } = await useAsyncData('stores', () => getStores({ category: 'CHICKEN' }))

// 가게 등록
async function handleCreate(form: CreateStoreRequest) {
  const res = await createStore(form)
  if (res.success) navigateTo(`/stores/${res.data.id}`)
}
</script>
```

### 각 서비스별 엔드포인트 요약

| 서비스 | 함수명 | 메서드 | 경로 |
|--------|--------|--------|------|
| **Auth** | `login()` | POST | `/api/v1/auth/login` |
| | `refresh()` | POST | `/api/v1/auth/refresh` |
| | `logout()` | POST | `/api/v1/auth/logout` |
| **User** | `getUser(email)` | GET | `/api/v1/users?email=` |
| | `getProfile(id)` | GET | `/api/v1/users/{id}/profile` |
| | `updateProfile(id, body)` | PUT | `/api/v1/users/{id}/profile` |
| | `deleteUser(id)` | DELETE | `/api/v1/users/{id}` |
| | `getAddresses()` | GET | `/api/v1/users/me/addresses` |
| | `createAddress(body)` | POST | `/api/v1/users/me/addresses` |
| | `updateAddress(id, body)` | PUT | `/api/v1/users/me/addresses/{id}` |
| | `deleteAddress(id)` | DELETE | `/api/v1/users/me/addresses/{id}` |
| **Store** | `getStores(query)` | GET | `/api/v1/stores` |
| | `getStore(id)` | GET | `/api/v1/stores/{id}` |
| | `createStore(body)` | POST | `/api/v1/stores` |
| **Menu** | `getMenus(storeId)` | GET | `/api/v1/stores/{storeId}/menus` |
| | `createMenu(storeId, body)` | POST | `/api/v1/stores/{storeId}/menus` |
| | `updateMenu(storeId, menuId, body)` | PUT | `/api/v1/stores/{storeId}/menus/{menuId}` |
| | `deleteMenu(storeId, menuId)` | DELETE | `/api/v1/stores/{storeId}/menus/{menuId}` |
| **Order** | `createOrder(body)` | POST | `/api/v1/orders` |
| | `getOrder(id)` | GET | `/api/v1/orders/{id}` |
| | `updateOrderStatus(id, status)` | PATCH | `/api/v1/orders/{id}/status` |
| **Delivery** | `createDelivery(body)` | POST | `/api/v1/deliveries` |
| | `getDelivery(id)` | GET | `/api/v1/deliveries/{id}` |
| | `updateDeliveryStatus(id, status)` | PATCH | `/api/v1/deliveries/{id}/status` |
| **Event** | `createEvent(body)` | POST | `/api/v1/events` |
| | `getEvent(id)` | GET | `/api/v1/events/{id}` |

---

## 6. 에러 처리

### Backend 공통 응답

```typescript
interface ApiResponse<T> {
  success: boolean
  data: T
  error: string | null
}
```

### 프론트엔드 에러 처리 패턴

```typescript
try {
  const res = await createOrder(orderData)
  if (!res.success) {
    // API 레벨 비즈니스 에러
    toast.add({ title: '오류', description: res.error || '요청에 실패했습니다.', color: 'error' })
    return
  }
  // 성공 처리
  toast.add({ title: '성공', description: '주문이 생성되었습니다.', color: 'success' })
} catch (err: any) {
  // 네트워크/서버 에러
  toast.add({ title: '서버 오류', description: err.message, color: 'error' })
}
```

### HTTP 상태 코드 매핑

| 코드 | 의미 | 프론트 처리 |
|------|------|------------|
| 200 | 성공 | `res.data` 사용 |
| 400 | 잘못된 요청 | 에러 메시지 토스트 표시 |
| 401 | 인증 실패 | 자동 토큰 갱신 → 실패 시 /login |
| 403 | 권한 없음 | /unauthorized 이동 |
| 404 | 리소스 없음 | "찾을 수 없음" 안내 |
| 500 | 서버 오류 | 서버 오류 토스트 표시 |

---

## 7. 페이지에서 사용하기

### 예시: 가게 목록 페이지

```vue
<script setup lang="ts">
const { getStores } = useStoreApi()
const toast = useToast()

const { data: storeList, status, refresh } = await useAsyncData(
  'store-list',
  () => getStores(),
  { default: () => [] }
)

const stores = computed(() => storeList.value ?? [])
</script>

<template>
  <UDashboardPanel>
    <div v-if="status === 'pending'">로딩 중...</div>
    <div v-else-if="status === 'error'">데이터를 불러올 수 없습니다.</div>
    <UTable v-else :rows="stores" />
  </UDashboardPanel>
</template>
```

### 예시: 주문 상태 변경

```vue
<script setup lang="ts">
const { updateOrderStatus } = useOrderApi()
const toast = useToast()

async function confirmOrder(orderId: string) {
  const res = await updateOrderStatus(orderId, 'CONFIRMED')
  if (res.success) {
    toast.add({ title: '성공', description: '주문이 확인되었습니다.' })
  }
}
</script>
```

---

## 8. 체크리스트

### 백엔드 실행 전 확인

- [ ] PostgreSQL 데이터베이스 실행 중
- [ ] Eureka Discovery 서비스 실행 (`service-discovery`, port 8100)
- [ ] Gateway 서비스 실행 (`service-gateway`, port 8000)
- [ ] 필요한 도메인 서비스 실행 (auth, user, store, order, delivery, event)

### 프론트엔드 실행 전 확인

- [ ] `.env` 파일에 `NUXT_PUBLIC_API_BASE_URL` 설정 확인
- [ ] `pnpm install` 완료
- [ ] `pnpm dev` 실행 (port 3000)

### API 연동 확인

- [ ] 로그인 → 토큰 발급 확인 (DevTools → Application → Cookies)
- [ ] 인증 필요 API 호출 시 `Authorization` 헤더 첨부 확인
- [ ] 401 발생 시 자동 토큰 갱신 동작 확인
- [ ] CORS 에러 없음 확인 (Nitro 프록시가 해결)

### 개발 팁

```bash
# 백엔드 전체 서비스 빌드 (프로젝트 루트에서)
cd backend/msa-root
./gradlew bootJar

# 프론트엔드 개발 서버
cd frontend/web-admin
pnpm dev

# 프론트엔드 빌드 확인
pnpm build
pnpm preview
```

---

> 📌 이 가이드에서 참조하는 코드 파일들은 `app/composables/api/`, `app/types/api.ts`에 위치합니다.
> 📌 Backend API 스펙은 `backend/msa-root/API_DOCUMENTATION.md`를 참조하세요.
