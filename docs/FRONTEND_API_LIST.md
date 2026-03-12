# 프론트엔드 API 목록

> **기준일**: 2026-03-10
> **Base URL**: `http://localhost:8000` (Gateway → 각 서비스로 프록시)
> **응답 포맷**: 모든 응답은 `ApiResponse<T>` 래퍼로 감싸여 반환됩니다.

```json
{
  "success": true,
  "code": 200,
  "message": "요청이 성공했습니다.",
  "data": {}
}
```

> **인증 방식**: access-token / refresh-token은 httpOnly 쿠키로 관리됩니다.
> Gateway는 access-token 쿠키 또는 `Authorization: Bearer <accessToken>` 헤더를 검증할 수 있습니다.
> `🔓 인증 불필요` / `🔐 인증 필요 (쿠키 기반 인증)` 로 구분합니다.

---

## 1. 인증 (Auth) `/api/v1/auth/**`

> 🔓 인증 불필요

### 1-1. 이메일 로그인

```http
POST /api/v1/auth/login
```

#### Request Body

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Response — `data: LoginResponse`

```json
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "홍길동",
  "role": "USER"
}
```

> access-token / refresh-token은 응답 본문이 아니라 httpOnly 쿠키로 설정됩니다.

---

### 1-2. 소셜 로그인 (Google OAuth2)

```http
POST /api/v1/auth/social?provider=google&token={idToken}
```

Query Params: `provider=google`, `token={Google ID Token}`

Response: `data: LoginResponse` (1-1과 동일)

---

### 1-3. 토큰 갱신

```http
POST /api/v1/auth/refresh
```

#### Request Body

없음. refresh-token 쿠키가 자동 전송됩니다.

Response: `data: LoginResponse` (1-1과 동일)

---

### 1-4. 로그아웃

```http
POST /api/v1/auth/logout
```

#### Request Body

없음. refresh-token 쿠키가 자동 전송됩니다.

Response: `data: null`

---

## 2. 회원 (User) `/api/v1/users/**`

### 2-1. 회원 가입

> 🔓 인증 불필요

```http
POST /api/v1/users/register
```

#### Request Body

```json
{
  "email": "user@example.com",
  "password": "password123",
  "name": "홍길동"
}
```

> `password` 최소 8자

#### Response — `data: UserDto`

```json
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "홍길동",
  "roles": "ROLE_USER",
  "createdAt": "2026-03-04T00:00:00Z"
}
```

---

### 2-2. 이메일로 유저 조회

> 🔐 인증 필요
> 프로필 조회/수정은 본인 또는 관리자만 가능합니다.

```http
GET /api/v1/users?email={email}
```

Response: `data: UserDto` (2-1과 동일)

---

### 2-3. 프로필 조회

> 🔐 인증 필요

```http
GET /api/v1/users/{id}/profile
```

#### Response — `data: UserProfileDto`

```json
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "홍길동",
  "phone": "010-1234-5678"
}
```

---

### 2-4. 프로필 수정

> 🔐 인증 필요

```http
PUT /api/v1/users/{id}/profile
```

#### Request Body

```json
{
  "name": "홍길동",
  "phone": "010-1234-5678"
}
```

Response: `data: UserProfileDto` (2-3과 동일)

---

### 2-5. 회원 탈퇴

> 🔐 인증 필요

```http
DELETE /api/v1/users/{id}
```

Response: `data: null`

---

## 3. 배송지 (Address) `/api/v1/users/me/addresses/**`

> 🔐 인증 필요
> ⚠️ Gateway가 JWT에서 추출한 `X-User-Id`, `X-User-Role` 헤더를 자동으로 전달합니다. 프론트에서 직접 세팅하지 않습니다.

### 3-1. 배송지 목록 조회

```http
GET /api/v1/users/me/addresses
```

#### Response — `data: AddressDto[]`

```json
[
  {
    "id": "uuid",
    "userId": "uuid",
    "label": "집",
    "line1": "서울시 강남구 테헤란로 123",
    "line2": "101동 202호",
    "city": "서울",
    "state": "서울특별시",
    "postalCode": "06234",
    "country": "KR",
    "primaryAddress": true,
    "createdAt": "2026-03-04T00:00:00Z"
  }
]
```

---

### 3-2. 배송지 추가

```http
POST /api/v1/users/me/addresses
```

#### Request Body — `AddressDto` (id, userId, createdAt 제외)

```json
{
  "label": "회사",
  "line1": "서울시 마포구 ...",
  "line2": "",
  "city": "서울",
  "state": "서울특별시",
  "postalCode": "04100",
  "country": "KR",
  "primaryAddress": false
}
```

Response: `data: AddressDto`

---

### 3-3. 배송지 수정

```http
PUT /api/v1/users/me/addresses/{addressId}
```

Request Body: `AddressDto` (3-2와 동일)

Response: `data: AddressDto`

---

### 3-4. 배송지 삭제

```http
DELETE /api/v1/users/me/addresses/{addressId}
```

Response: `data: null`

---

## 4. 가게 (Store) `/api/v1/stores/**`

> 🔐 인증 필요

### 4-1. 가게 목록 조회

```http
GET /api/v1/stores?category={category}&status={status}
```

Query Params (선택):

- `category`: 카테고리 (예: `KOREAN`, `CHINESE`, `PIZZA` 등)
- `status`: `OPEN` | `CLOSED` | `PREPARING`

#### Response — `data: StoreDto[]`

```json
[
  {
    "id": "uuid",
    "name": "맛있는 치킨",
    "address": "서울시 강남구 ...",
    "phone": "02-1234-5678",
    "category": "CHICKEN",
    "status": "OPEN",
    "latitude": 37.5012,
    "longitude": 127.0396,
    "minOrderAmount": 15000,
    "ratingAvg": 4.5,
    "description": "바삭바삭한 치킨 전문점",
    "openingHours": "10:00~22:00",
    "ownerId": "uuid",
    "eta": "26~33분",
    "reviewCount": 0,
    "deliveryFee": "무료배달",
    "heroIcon": "i-lucide-drumstick",
    "tags": ["인기", "바삭", "야식"],
    "bestseller": "후라이드 치킨",
    "promo": "후라이드 치킨 메뉴를 바로 장바구니에 담을 수 있습니다."
  }
]
```

> customer 웹은 위 확장 필드를 우선 사용하여 배달시간, 태그, 프로모션 문구를 렌더링합니다.

---

### 4-2. 가게 상세 조회

```http
GET /api/v1/stores/{id}
```

Response: `data: StoreDto` (4-1 배열 요소와 동일)

---

### 4-3. 가게 등록 (사장님용)

```http
POST /api/v1/stores
```

#### Request Body

```json
{
  "name": "맛있는 치킨",
  "address": "서울시 강남구 ...",
  "phone": "02-1234-5678",
  "category": "CHICKEN",
  "status": "OPEN",
  "latitude": 37.5012,
  "longitude": 127.0396,
  "minOrderAmount": 15000,
  "description": "바삭바삭한 치킨 전문점",
  "openingHours": "10:00~22:00",
  "ownerId": "uuid"
}
```

> `name`, `address`, `ownerId` 필수

Response: `data: StoreDto`

---

## 5. 메뉴 (Menu) `/api/v1/stores/{storeId}/menus/**`

> 🔐 인증 필요

### 5-1. 메뉴 목록 조회

```http
GET /api/v1/stores/{storeId}/menus
```

#### Response — `data: MenuDto[]`

```json
[
  {
    "id": "uuid",
    "storeId": "uuid",
    "name": "후라이드 치킨",
    "description": "바삭바삭한 후라이드",
    "price": 18000,
    "available": true,
    "createdAt": "2026-03-04T00:00:00Z"
  }
]
```

---

### 5-2. 메뉴 등록 (사장님용)

```http
POST /api/v1/stores/{storeId}/menus
```

#### Request Body

```json
{
  "name": "후라이드 치킨",
  "description": "바삭바삭한 후라이드",
  "price": 18000,
  "available": true
}
```

Response: `data: MenuDto`

---

### 5-3. 메뉴 수정 (사장님용)

```http
PUT /api/v1/stores/{storeId}/menus/{menuId}
```

Request Body: (5-2와 동일)

Response: `data: MenuDto`

---

### 5-4. 메뉴 삭제 (사장님용)

```http
DELETE /api/v1/stores/{storeId}/menus/{menuId}
```

Response: `data: null`

---

## 6. 주문 (Order) `/api/v1/orders/**`

> 🔐 인증 필요

### 6-1. 주문 생성

```http
POST /api/v1/orders
```

#### Request Body

```json
{
  "address": "서울시 강남구 ...",
  "totalAmount": 18000,
  "items": [
    {
      "productId": "uuid",
      "quantity": 2,
      "price": 9000
    }
  ]
}
```

> `address` 필수 / `totalAmount` 0 이상 / `items` 최소 1개 / `quantity` 1 이상
> `userId`는 요청 바디가 아니라 인증 쿠키 기반 로그인 세션에서 Gateway가 주입한 `X-User-Id`로 결정됩니다.

#### Response — `data: OrderDto`

```json
{
  "id": "uuid",
  "userId": "uuid",
  "storeId": "uuid",
  "totalAmount": 18000,
  "items": [{ "productId": "uuid", "quantity": 2, "price": 9000 }],
  "status": "CREATED",
  "createdAt": "2026-03-04T00:00:00Z"
}
```

---

### 6-2. 주문 조회

```http
GET /api/v1/orders/{id}
```

Response: `data: OrderDto` (6-1과 동일)

---

### 6-3. 내 주문 목록 조회

```http
GET /api/v1/orders/my
```

Response: `data: OrderDto[]`

---

### 6-4. 매장 주문 목록 조회 (사장님용)

```http
GET /api/v1/orders/store/{storeId}
```

Response: `data: OrderDto[]`

---

### 6-5. 주문 상태 변경 (사장님/내부용)

```http
PATCH /api/v1/orders/{id}/status
```

#### Request Body

```json
{
  "status": "COOKING"
}
```

상태 흐름: `CREATED` → `COOKING` → `DELIVERING` → `DONE` | `CANCELLED`

Response: `data: OrderDto`

---

## 7. 배달 (Delivery) `/api/v1/deliveries/**`

> 🔐 인증 필요

### 7-1. 배달 생성

```http
POST /api/v1/deliveries
```

#### Request Body

```json
{
  "orderId": "uuid",
  "address": "서울시 강남구 ...",
  "courier": "라이더 이름"
}
```

> `orderId` 필수

#### Response — `data: DeliveryDto`

```json
{
  "id": "uuid",
  "orderId": "uuid",
  "courier": "라이더 이름",
  "status": "SCHEDULED",
  "deliveryFee": 3000,
  "scheduledAt": "2026-03-04T01:00:00Z"
}
```

---

### 7-2. 배달 조회

```http
GET /api/v1/deliveries/{id}
```

Response: `data: DeliveryDto` (7-1과 동일)

---

### 7-3. 배달 상태 변경

```http
PATCH /api/v1/deliveries/{id}/status
```

#### Request Body

```json
{
  "status": "IN_PROGRESS"
}
```

상태 흐름: `SCHEDULED` → `IN_PROGRESS` → `DELIVERED` | `FAILED`

Response: `data: DeliveryDto`

---

## 8. 이벤트 (Event) `/api/v1/events/**`

> 🔐 인증 필요

### 8-1. 이벤트 발행

```http
POST /api/v1/events
```

#### Request Body

```json
{
  "type": "ORDER_CREATED",
  "payload": "{\"orderId\":\"uuid\"}"
}
```

> `type` 필수 / `payload`는 직렬화된 JSON 문자열 (String)

#### Response — `data: EventDto`

```json
{
  "id": "uuid",
  "type": "ORDER_CREATED",
  "payload": "{\"orderId\":\"uuid\"}",
  "createdAt": "2026-03-04T00:00:00Z"
}
```

---

### 8-2. 이벤트 조회

```http
GET /api/v1/events/{id}
```

Response: `data: EventDto` (8-1과 동일)

---

## 9. 내부 전용 엔드포인트 (프론트 직접 호출 금지)

> ⛔ 아래 엔드포인트는 서비스 간 내부 호출(gRPC 대체 경로)에 사용됩니다.
> Gateway에서 인증 필터가 적용되므로 프론트에서 직접 호출하지 않습니다.

### 9-1. 사용자 인증 확인 (service-auth → service-user 내부 호출)

```http
POST /api/v1/users/authenticate
```

#### Request Body

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Response — `data: AuthUserDto`

```json
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "홍길동",
  "roles": "ROLE_USER"
}
```

---

## 부록 A. 에러 응답

```json
{
  "success": false,
  "code": 400,
  "message": "입력값이 올바르지 않습니다.",
  "data": null
}
```

| HTTP 상태 | 의미 |
| --- | --- |
| `400` | 요청 값 오류 (Validation 실패, Bad Request) |
| `401` | 인증 토큰 없음 또는 만료 |
| `403` | 권한 없음 |
| `404` | 리소스 없음 |
| `409` | 충돌 (이미 존재하는 이메일 등) |
| `500` | 서버 내부 오류 |

---

## 부록 B. 앱별 주요 사용 API

| API | web-user | web-shop (사장님) | web-admin |
| --- | :---: | :---: | :---: |
| 로그인/소셜로그인/토큰갱신 | ✅ | ✅ | ✅ |
| 회원가입 | ✅ | ✅ | — |
| 프로필 조회/수정 | ✅ | ✅ | ✅ |
| 배송지 CRUD | ✅ | — | — |
| 가게 목록/상세 | ✅ | ✅ | ✅ |
| 가게 등록 | — | ✅ | ✅ |
| 메뉴 목록 | ✅ | ✅ | ✅ |
| 메뉴 등록/수정/삭제 | — | ✅ | ✅ |
| 주문 생성 | ✅ | — | — |
| 주문 조회 | ✅ | ✅ | ✅ |
| 주문 상태 변경 | — | ✅ | ✅ |
| 배달 조회 | ✅ | ✅ | ✅ |
| 배달 상태 변경 | — | — | ✅ |
| 이벤트 발행/조회 | — | — | ✅ |

---

## 부록 C. Nuxt 프록시 설정 참고

각 프론트 앱은 `server/` 프록시를 통해 Gateway를 호출합니다.
`localhost:8000`을 직접 호출하지 않고 Nuxt 내부 프록시 경로 `/api/v1/**`를 사용하세요.

```ts
const { $api } = useApi()

// 가게 목록 조회
const stores = await $api<StoreDto[]>('/api/v1/stores?category=CHICKEN')

// 주문 생성
const order = await $api<OrderDto>('/api/v1/orders', {
  method: 'POST',
  body: { userId, address, totalAmount, items },
})
```
