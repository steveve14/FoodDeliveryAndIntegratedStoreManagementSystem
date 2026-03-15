# 🍔 Food Delivery & Integrated Store Management System — API 문서

> **작성일**: 2026-02-28  
> **버전**: v1  
> **대상**: 프론트엔드 개발자

---

## 📌 목차

1. [아키텍처 개요](#1-아키텍처-개요)
2. [공통 규칙](#2-공통-규칙)
3. [인증 서비스 — Auth Service](#3-인증-서비스--auth-service-port-7000)
4. [회원 서비스 — User Service](#4-회원-서비스--user-service-port-8010)
5. [가게 서비스 — Store Service](#5-가게-서비스--store-service-port-8020)
6. [주문 서비스 — Order Service](#6-주문-서비스--order-service-port-8040)
7. [배달 서비스 — Delivery Service](#7-배달-서비스--delivery-service-port-8050)
8. [이벤트 서비스 — Event Service](#8-이벤트-서비스--event-service-port-8030)
9. [공통 응답 형식](#9-공통-응답-형식)
10. [에러 코드](#10-에러-코드)
11. [인증 흐름 가이드](#11-인증-흐름-가이드)

---

## 1. 아키텍처 개요

```
[프론트엔드]
     │
     ▼ HTTP (port 8000)
[API Gateway — service-gateway]
     │  JWT 토큰 검증 (AuthorizationHeaderFilter)
     │  X-User-Id 헤더 주입
     ├──▶ /auth/**   → service-auth   (port 7000)
     ├──▶ /users/**  → service-user   (port 8010)
     ├──▶ /stores/** → service-store  (port 8020)
     ├──▶ /events/** → service-event  (port 8030)
     ├──▶ /orders/** → service-order  (port 8040)
     └──▶ /deliveries/** → service-delivery (port 8050)
```

### 포트 요약

| 서비스 | HTTP 포트 | gRPC 포트 |
|--------|-----------|-----------|
| Gateway | **8000** | — |
| Discovery (Eureka) | 8100 | — |
| Auth | 7000 | 9000 |
| User | 8010 | 9010 |
| Store | 8020 | 9020 |
| Event | 8030 | 9030 |
| Order | 8040 | 9040 |
| Delivery | 8050 | 9050 |

> **프론트엔드는 Gateway(8000) 하나만 바라봅니다.**  
> 개별 서비스 포트는 서비스 간 gRPC 통신에만 사용됩니다.

---

## 2. 공통 규칙

### Base URL
```
http://localhost:8000
```
> 운영 환경에서는 도메인으로 교체하세요.

### 인증 방식

- **Bearer Token** (JWT)
- 로그인 후 발급된 `accessToken`을 아래 헤더에 포함해 전송합니다.

```http
Authorization: Bearer <accessToken>
```

- **인증 불필요** 엔드포인트:
  - `POST /auth/login`
  - `POST /auth/refresh`
  - `POST /auth/social`
  - `POST /users/register` *(회원가입)*

- **인증 필요** 엔드포인트: 위를 제외한 **모든 API**

> Gateway가 토큰을 검증하고 사용자 ID를 `X-User-Id` 헤더로 각 서비스에 전달합니다.  
> 프론트엔드는 `X-User-Id`를 직접 설정하지 않습니다.

### Content-Type
```http
Content-Type: application/json
```

### 공통 응답 구조
```json
{
  "success": true,
  "data": { ... },
  "error": null
}
```
| 필드 | 타입 | 설명 |
|------|------|------|
| `success` | boolean | 성공 여부 |
| `data` | Object / Array / null | 실제 데이터 |
| `error` | String / null | 오류 메시지 (실패 시) |

---

## 3. 인증 서비스 — Auth Service (port 7000)

> **Gateway 경로**: `/auth/**` → `service-auth`  
> **인증 불필요**

---

### 3-1. 이메일/비밀번호 로그인

```
POST /auth/login
```

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "plaintext_password"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `email` | string | ✅ | 사용자 이메일 |
| `password` | string | ✅ | 비밀번호 (평문, HTTPS 필수) |

**Response 200**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "error": null
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `accessToken` | string | 유효기간 1시간 (3600000ms) |
| `refreshToken` | string | 유효기간 2주 (1209600000ms) |

---

### 3-2. 소셜 로그인

```
POST /auth/social?provider={provider}&token={token}
```

**Query Parameters**

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `provider` | string | ✅ | 소셜 제공자 (예: `google`, `kakao`) |
| `token` | string | ✅ | 소셜에서 발급된 OAuth 액세스 토큰 |

**Response 200**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "error": null
}
```

---

### 3-3. 토큰 갱신

```
POST /auth/refresh
```

**Request Body**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "error": null
}
```

> ⚠️ accessToken 만료 전에 갱신하는 것을 권장합니다. 갱신 시 새 refreshToken도 함께 저장하세요.

---

### 3-4. 로그아웃

```
POST /auth/logout
```

**Request Body**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response 200**
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

> 서버에서 refreshToken을 무효화합니다. 클라이언트는 저장된 토큰을 삭제하세요.

---

## 4. 회원 서비스 — User Service (port 8010)

> **Gateway 경로**: `/users/**` → `service-user`

---

### 4-1. 회원가입

```
POST /users/register
```

> **인증 불필요**

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "plaintext_password",
  "name": "홍길동"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `email` | string | ✅ | 이메일 (중복 불가) |
| `password` | string | ✅ | 비밀번호 (서버에서 bcrypt 해싱) |
| `name` | string | ✅ | 사용자 이름 |

**Response 200**
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "name": "홍길동",
    "roles": "USER",
    "createdAt": "2026-02-28T00:00:00Z"
  },
  "error": null
}
```

**Error 400** — 이미 사용 중인 이메일
```json
{
  "error": "이미 존재하는 이메일입니다."
}
```

---

### 4-2. 이메일로 사용자 조회

```
GET /users?email={email}
```

> 🔒 **인증 필요**

**Query Parameters**

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `email` | string | ✅ | 조회할 이메일 |

**Response 200**
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "name": "홍길동",
    "roles": "USER",
    "createdAt": "2026-02-28T00:00:00Z"
  },
  "error": null
}
```

---

### 4-3. 프로필 조회

```
GET /users/{id}/profile
```

> 🔒 **인증 필요**

**Path Variables**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `id` | string | 사용자 UUID |

**Response 200**
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "name": "홍길동",
    "phone": "010-1234-5678"
  },
  "error": null
}
```

---

### 4-4. 프로필 수정

```
PUT /users/{id}/profile
```

> 🔒 **인증 필요**

**Path Variables**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `id` | string | 사용자 UUID |

**Request Body**
```json
{
  "name": "홍길동",
  "phone": "010-1234-5678"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `name` | string | ✅ | 변경할 이름 |
| `phone` | string | ❌ | 변경할 전화번호 |

**Response 200**
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "name": "홍길동",
    "phone": "010-1234-5678"
  },
  "error": null
}
```

---

### 4-5. 회원 탈퇴

```
DELETE /users/{id}
```

> 🔒 **인증 필요**

**Path Variables**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `id` | string | 사용자 UUID |

**Response 200**
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

### 4-6. 내 배송지 목록 조회

```
GET /users/me/addresses
```

> 🔒 **인증 필요** — Gateway가 `X-User-Id` 헤더를 자동 주입합니다.

**Response 200**
```json
{
  "success": true,
  "data": [
    {
      "id": "addr-uuid-1",
      "userId": "user-uuid",
      "label": "집",
      "line1": "서울시 강남구 테헤란로 1",
      "line2": "101호",
      "city": "서울",
      "state": "서울특별시",
      "postalCode": "06236",
      "country": "KR",
      "primaryAddress": true,
      "createdAt": "2026-02-28T00:00:00Z"
    }
  ],
  "error": null
}
```

---

### 4-7. 배송지 추가

```
POST /users/me/addresses
```

> 🔒 **인증 필요**

**Request Body**
```json
{
  "label": "집",
  "line1": "서울시 강남구 테헤란로 1",
  "line2": "101호",
  "city": "서울",
  "state": "서울특별시",
  "postalCode": "06236",
  "country": "KR",
  "primaryAddress": true
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `label` | string | ✅ | 라벨 (예: `집`, `회사`) |
| `line1` | string | ✅ | 도로명 주소 |
| `line2` | string | ❌ | 상세 주소 |
| `city` | string | ✅ | 시/구 |
| `state` | string | ✅ | 시/도 |
| `postalCode` | string | ✅ | 우편번호 |
| `country` | string | ✅ | 국가 코드 (예: `KR`) |
| `primaryAddress` | boolean | ❌ | 기본 배송지 여부 (기본값 false) |

**Response 200**
```json
{
  "success": true,
  "data": {
    "id": "addr-uuid-1",
    "userId": "user-uuid",
    "label": "집",
    "line1": "서울시 강남구 테헤란로 1",
    "line2": "101호",
    "city": "서울",
    "state": "서울특별시",
    "postalCode": "06236",
    "country": "KR",
    "primaryAddress": true,
    "createdAt": "2026-02-28T00:00:00Z"
  },
  "error": null
}
```

---

### 4-8. 배송지 수정

```
PUT /users/me/addresses/{addressId}
```

> 🔒 **인증 필요**

**Path Variables**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `addressId` | string | 배송지 UUID |

**Request Body** — 4-7과 동일한 구조

**Response 200** — 수정된 배송지 객체 반환

---

### 4-9. 배송지 삭제

```
DELETE /users/me/addresses/{addressId}
```

> 🔒 **인증 필요**

**Response 200**
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

## 5. 가게 서비스 — Store Service (port 8020)

> **Gateway 경로**: `/stores/**`, `/menus/**` → `service-store`

---

### 5-1. 가게 목록 조회

```
GET /stores?category={category}&status={status}
```

> 🔒 **인증 필요**

**Query Parameters**

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `category` | string | ❌ | 카테고리 필터 (예: `KOREAN`, `CHINESE`, `PIZZA`) |
| `status` | string | ❌ | 상태 필터 (`OPEN`, `CLOSED`) |

**Response 200**
```json
{
  "success": true,
  "data": [
    {
      "id": "store-uuid-1",
      "name": "맛있는 치킨",
      "address": "서울시 강남구 역삼동 1번지",
      "phone": "02-1234-5678",
      "category": "CHICKEN",
      "status": "OPEN",
      "latitude": 37.5012,
      "longitude": 127.0396,
      "minOrderAmount": 12000,
      "ratingAvg": 4.5,
      "description": "바삭바삭한 치킨 전문점",
      "openingHours": "11:00-23:00",
      "ownerId": "owner-uuid"
    }
  ],
  "error": null
}
```

---

### 5-2. 가게 단건 조회

```
GET /stores/{id}
```

> 🔒 **인증 필요**

**Path Variables**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `id` | string | 가게 UUID |

**Response 200** — 가게 객체 단건 반환 (5-1의 배열 항목과 동일한 구조)

---

### 5-3. 가게 등록

```
POST /stores
```

> 🔒 **인증 필요**

**Request Body**
```json
{
  "name": "맛있는 치킨",
  "address": "서울시 강남구 역삼동 1번지",
  "phone": "02-1234-5678",
  "category": "CHICKEN",
  "status": "OPEN",
  "latitude": 37.5012,
  "longitude": 127.0396,
  "minOrderAmount": 12000,
  "ratingAvg": 0.0,
  "description": "바삭바삭한 치킨 전문점",
  "openingHours": "11:00-23:00",
  "ownerId": "owner-uuid"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `name` | string | ✅ | 가게 이름 |
| `address` | string | ✅ | 주소 |
| `phone` | string | ❌ | 전화번호 |
| `category` | string | ✅ | 카테고리 |
| `status` | string | ✅ | `OPEN` / `CLOSED` |
| `latitude` | double | ❌ | 위도 |
| `longitude` | double | ❌ | 경도 |
| `minOrderAmount` | long | ❌ | 최소 주문 금액 (원) |
| `ratingAvg` | double | ❌ | 평균 별점 (초기값 0) |
| `description` | string | ❌ | 가게 설명 |
| `openingHours` | string | ❌ | 영업 시간 |
| `ownerId` | string | ✅ | 사장님 사용자 UUID |

**Response 200** — 생성된 가게 객체 반환

---

### 5-4. 메뉴 목록 조회

```
GET /stores/{storeId}/menus
```

> 🔒 **인증 필요**

**Path Variables**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `storeId` | string | 가게 UUID |

**Response 200**
```json
{
  "success": true,
  "data": [
    {
      "id": "menu-uuid-1",
      "storeId": "store-uuid-1",
      "name": "후라이드 치킨",
      "description": "바삭바삭한 후라이드",
      "price": 18000,
      "available": true,
      "createdAt": "2026-02-28T00:00:00Z"
    }
  ],
  "error": null
}
```

---

### 5-5. 메뉴 추가

```
POST /stores/{storeId}/menus
```

> 🔒 **인증 필요**

**Request Body**
```json
{
  "name": "후라이드 치킨",
  "description": "바삭바삭한 후라이드",
  "price": 18000,
  "available": true
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `name` | string | ✅ | 메뉴 이름 |
| `description` | string | ❌ | 설명 |
| `price` | long | ✅ | 가격 (원) |
| `available` | boolean | ✅ | 판매 가능 여부 |

**Response 200** — 생성된 메뉴 객체 반환

---

### 5-6. 메뉴 수정

```
PUT /stores/{storeId}/menus/{menuId}
```

> 🔒 **인증 필요**

**Request Body** — 5-5와 동일한 구조

**Response 200** — 수정된 메뉴 객체 반환

---

### 5-7. 메뉴 삭제

```
DELETE /stores/{storeId}/menus/{menuId}
```

> 🔒 **인증 필요**

**Response 200**
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

## 6. 주문 서비스 — Order Service (port 8040)

> **Gateway 경로**: `/orders/**` → `service-order`  
> 🔒 **모든 엔드포인트 인증 필요**

---

### 6-1. 주문 생성

```
POST /orders
```

**Request Body**
```json
{
  "userId": "user-uuid",
  "items": [
    {
      "productId": "menu-uuid-1",
      "quantity": 2,
      "price": 18000
    },
    {
      "productId": "menu-uuid-2",
      "quantity": 1,
      "price": 5000
    }
  ],
  "address": "서울시 강남구 역삼동 1번지 101호",
  "totalAmount": 41000
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `userId` | string | ✅ | 주문자 UUID |
| `items` | array | ✅ | 주문 상품 목록 |
| `items[].productId` | string | ✅ | 메뉴(상품) UUID |
| `items[].quantity` | int | ✅ | 수량 |
| `items[].price` | long | ✅ | 단가 (원) |
| `address` | string | ❌ | 배송 주소 |
| `totalAmount` | long | ✅ | 총 주문 금액 (원) |

**Response 200**
```json
{
  "success": true,
  "data": {
    "id": "order-uuid-1",
    "userId": "user-uuid",
    "storeId": "store-uuid-1",
    "totalAmount": 41000,
    "items": [
      {
        "productId": "menu-uuid-1",
        "quantity": 2,
        "price": 18000
      }
    ],
    "status": "CREATED",
    "createdAt": "2026-02-28T00:00:00Z"
  },
  "error": null
}
```

**주문 상태 값**

| 상태 | 설명 |
|------|------|
| `CREATED` | 주문 생성됨 |
| `CONFIRMED` | 가게 확인 완료 |
| `PREPARING` | 조리 중 |
| `READY` | 조리 완료 (배달 대기) |
| `DELIVERING` | 배달 중 |
| `COMPLETED` | 배달 완료 |
| `CANCELLED` | 주문 취소 |

---

### 6-2. 주문 단건 조회

```
GET /orders/{id}
```

**Path Variables**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `id` | string | 주문 UUID |

**Response 200** — 주문 객체 반환 (6-1의 data와 동일 구조)

---

### 6-3. 주문 상태 변경

```
PATCH /orders/{id}/status
```

**Path Variables**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `id` | string | 주문 UUID |

**Request Body**
```json
{
  "status": "CONFIRMED"
}
```

**Response 200** — 변경된 주문 객체 반환

---

## 7. 배달 서비스 — Delivery Service (port 8050)

> **Gateway 경로**: `/deliveries/**` → `service-delivery`  
> 🔒 **모든 엔드포인트 인증 필요**

---

### 7-1. 배달 생성

```
POST /deliveries
```

**Request Body**
```json
{
  "orderId": "order-uuid-1",
  "address": "서울시 강남구 역삼동 1번지 101호",
  "courier": "김라이더"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `orderId` | string | ✅ | 연결할 주문 UUID |
| `address` | string | ✅ | 배달 주소 |
| `courier` | string | ❌ | 배달 기사 이름/ID |

**Response 200**
```json
{
  "success": true,
  "data": {
    "id": "delivery-uuid-1",
    "orderId": "order-uuid-1",
    "courier": "김라이더",
    "status": "SCHEDULED",
    "deliveryFee": 0,
    "scheduledAt": "2026-02-28T01:00:00Z"
  },
  "error": null
}
```

**배달 상태 값**

| 상태 | 설명 |
|------|------|
| `SCHEDULED` | 배달 예약됨 |
| `PENDING` | 배달 대기 |
| `PICKED_UP` | 픽업 완료 |
| `IN_TRANSIT` | 배달 중 |
| `DELIVERED` | 배달 완료 |
| `CANCELLED` | 배달 취소 |

---

### 7-2. 배달 단건 조회

```
GET /deliveries/{id}
```

**Path Variables**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `id` | string | 배달 UUID |

**Response 200** — 배달 객체 반환 (7-1의 data와 동일 구조)

---

### 7-3. 배달 상태 변경

```
PATCH /deliveries/{id}/status
```

**Request Body**
```json
{
  "status": "PICKED_UP"
}
```

**Response 200** — 변경된 배달 객체 반환

---

## 8. 이벤트 서비스 — Event Service (port 8030)

> **Gateway 경로**: `/events/**` → `service-event`  
> 🔒 **모든 엔드포인트 인증 필요**

---

### 8-1. 이벤트 생성

```
POST /events
```

**Request Body**
```json
{
  "type": "ORDER_COMPLETED",
  "payload": "{\"orderId\": \"order-uuid-1\", \"userId\": \"user-uuid\"}"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `type` | string | ✅ | 이벤트 타입 (예: `ORDER_COMPLETED`, `COUPON_ISSUED`) |
| `payload` | string | ✅ | 이벤트 데이터 (JSON 직렬화 문자열) |

**Response 200**
```json
{
  "success": true,
  "data": {
    "id": "event-uuid-1",
    "type": "ORDER_COMPLETED",
    "payload": "{\"orderId\": \"order-uuid-1\"}",
    "createdAt": "2026-02-28T00:00:00Z"
  },
  "error": null
}
```

---

### 8-2. 이벤트 단건 조회

```
GET /events/{id}
```

**Path Variables**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `id` | string | 이벤트 UUID |

**Response 200** — 이벤트 객체 반환 (8-1의 data와 동일 구조)

---

## 9. 공통 응답 형식

### 성공 응답
```json
{
  "success": true,
  "data": { ... },
  "error": null
}
```

### 실패 응답
```json
{
  "success": false,
  "data": null,
  "error": "에러 메시지"
}
```

### HTTP 상태 코드

| 코드 | 의미 |
|------|------|
| `200` | 성공 |
| `400` | 잘못된 요청 (Bad Request) |
| `401` | 인증 실패 (Unauthorized) — 토큰 없음 또는 유효하지 않음 |
| `403` | 권한 없음 (Forbidden) |
| `404` | 리소스 없음 (Not Found) |
| `500` | 서버 오류 (Internal Server Error) |

> ⚠️ 현재 API Gateway는 인증 실패 시 **HTTP 401**을 반환하며, response body가 없을 수 있습니다.

---

## 10. 에러 코드

### 공통 에러 응답 형식
```json
{
  "error": "에러 메시지"
}
```

| 상황 | HTTP | 메시지 예시 |
|------|------|-------------|
| 이메일 중복 | 400 | `이미 존재하는 이메일입니다.` |
| 사용자 없음 | 404 | `사용자를 찾을 수 없습니다.` |
| 인증 실패 | 401 | `Unauthorized` |
| 주소 없음 | 400 | `Address not found` |
| 메뉴 없음 | 400 | `Menu not found` |
| 권한 없음 | 400 | `Forbidden` |
| 리소스 없음 | 200 | `Not found` *(현재 일부 API가 200 + error 반환)* |
| 서버 오류 | 500 | `서버 오류가 발생했습니다.` |

---

## 11. 인증 흐름 가이드

### 로그인 → API 호출 흐름

```
1. POST /auth/login  (또는 /auth/social)
   └── 응답: { accessToken, refreshToken }
   └── 클라이언트: localStorage / secure cookie에 저장

2. 인증 필요 API 호출 시
   └── Header: Authorization: Bearer {accessToken}

3. 401 응답 수신 시 (토큰 만료)
   └── POST /auth/refresh  { refreshToken }
   └── 새 accessToken, refreshToken 저장
   └── 실패한 요청 재시도

4. 로그아웃
   └── POST /auth/logout  { refreshToken }
   └── 클라이언트: 저장된 토큰 삭제
```

### JavaScript 예시 (Axios)

```javascript
// axios 인터셉터 설정
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 토큰 만료 자동 갱신
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      const refreshToken = localStorage.getItem('refreshToken');
      const { data } = await axios.post('/auth/refresh', { refreshToken });
      localStorage.setItem('accessToken', data.data.accessToken);
      localStorage.setItem('refreshToken', data.data.refreshToken);
      // 실패한 요청 재시도
      error.config.headers.Authorization = `Bearer ${data.data.accessToken}`;
      return axios(error.config);
    }
    return Promise.reject(error);
  }
);
```

---

## 12. 주요 비즈니스 플로우

### 주문 생성 전체 플로우

```
[프론트]                          [백엔드]
   │
   ├─ GET /stores?status=OPEN ──▶ 가게 목록 조회
   ├─ GET /stores/{id}/menus ───▶ 메뉴 조회
   ├─ POST /orders ─────────────▶ 주문 생성  (status: CREATED)
   ├─ POST /deliveries ─────────▶ 배달 생성  (status: SCHEDULED)
   └─ PATCH /orders/{id}/status ▶ 주문 상태 업데이트
```

### 토큰 만료 처리 흐름

```
API 호출 ──▶ 401 응답
              │
              ├─ POST /auth/refresh
              │     ├─ 성공: 새 토큰 저장 → 원래 요청 재시도
              │     └─ 실패 (refreshToken도 만료): 로그인 페이지로 이동
```

---

> 📌 **참고**: 현재 구현 기준으로 작성되었습니다. API 변경 시 이 문서를 함께 업데이트하세요.  
> 📌 **HTTPS**: 운영 환경에서는 반드시 HTTPS를 사용하세요. 비밀번호가 평문으로 전송됩니다.
