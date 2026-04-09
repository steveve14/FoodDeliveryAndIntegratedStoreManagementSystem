---
tags:
  - API
  - REST
  - 응답포맷
관련:
  - "[[04_마이크로서비스]]"
  - "[[02_시스템_아키텍처]]"
---

# 06. API 설계 표준

> **최종 수정**: 2026-04-05

---

## 📏 URI 네이밍 규칙

| 규칙 | 올바른 예 | 잘못된 예 |
|---|---|---|
| 소문자 사용 | `/api/v1/users` | `/api/v1/Users` |
| 하이픈(`-`) 단어 구분 | `/api/v1/cart-items` | `/api/v1/cart_items` |
| 복수형 리소스 명사 | `/api/v1/orders` | `/api/v1/order` |
| 계층 구조 | `/api/v1/users/1/orders` | `/api/v1/getUserOrders` |

### 기본 URL 패턴

```
/api/{version}/{resource}/{id}
/api/v1/users/1
/api/v1/stores/abc123/menus
```

---

## 🌐 HTTP Method 활용

| Method | 용도 | 멱등성 |
|---|---|---|
| `GET` | 리소스 조회 (Body 금지) | ✅ |
| `POST` | 리소스 생성 | ❌ |
| `PUT` | 리소스 전체 수정 | ✅ |
| `PATCH` | 리소스 일부 수정 | - |
| `DELETE` | 리소스 삭제 | ✅ |

---

## 📦 공통 응답 포맷

모든 API 응답은 `ApiResponse<T>` 객체로 감쌉니다.

### 성공 응답

```json
{
  "success": true,
  "code": 200,
  "message": "요청이 성공했습니다.",
  "data": {
    "orderId": "abc123",
    "totalPrice": 15000,
    "status": "COOKING"
  }
}
```

### 에러 응답

```json
{
  "success": false,
  "code": 400,
  "message": "재고가 부족하여 주문할 수 없습니다.",
  "data": null
}
```

> [!note] 예외 정책
> **파일/바이너리 전송** 엔드포인트는 `ApiResponse<T>` 래퍼를 사용하지 않습니다.
> 예: `GET /api/v1/users/avatars/{filename}`

---

## 🔑 인증 헤더 규약

| 방식 | 형식 | 적용 범위 |
|---|---|---|
| Bearer Token | `Authorization: Bearer {JWT}` | 일반 API 요청 |
| HttpOnly Cookie | `access-token` 쿠키 | 브라우저 기반 클라이언트 |

### Gateway가 전달하는 내부 헤더

```
X-User-Id:   {userId}    # JWT sub 클레임에서 추출
X-User-Role: {role}      # JWT role 클레임에서 추출
```

> [!warning] 헤더 신뢰 제한
> 내부 서비스는 Gateway가 전달한 헤더를 무조건 신뢰하지 않고,
> **JWT를 직접 파싱해 클레임과 대조**합니다. 스푸핑 방어 목적.

---

## 📋 주요 API 엔드포인트 목록

### service-auth (`/api/v1/auth`)

| Method | 경로 | 설명 | 인증 |
|---|---|---|---|
| POST | `/auth/login` | 로그인, JWT 발급 | ❌ |
| POST | `/auth/register` | 회원가입 | ❌ |
| POST | `/auth/refresh` | Access Token 갱신 | 쿠키 |
| POST | `/auth/logout` | 로그아웃, 쿠키 삭제 | ✅ |

### service-user (`/api/v1/users`)

| Method | 경로 | 설명 | 권한 |
|---|---|---|---|
| GET | `/users/me` | 내 정보 조회 | USER |
| PATCH | `/users/me` | 내 정보 수정 | USER |
| GET | `/users/me/addresses` | 주소 목록 | USER |
| GET | `/users/frontend/**` | 프론트 집계 | ADMIN |

### service-store (`/api/v1/stores`)

| Method | 경로 | 설명 | 권한 |
|---|---|---|---|
| GET | `/stores` | 매장 목록 | ❌ 공개 |
| GET | `/stores/{id}` | 매장 상세 | ❌ 공개 |
| GET | `/stores/{id}/menus` | 메뉴 목록 | ❌ 공개 |
| POST | `/stores` | 매장 등록 | OWNER |
| PUT | `/stores/{id}` | 매장 수정 | OWNER |

### service-order (`/api/v1/orders`)

| Method | 경로 | 설명 | 권한 |
|---|---|---|---|
| POST | `/orders` | 주문 생성 | USER |
| GET | `/orders/my` | 내 주문 목록 | USER |
| GET | `/orders/{id}` | 주문 상세 | USER/STORE/ADMIN |
| PATCH | `/orders/{id}/status` | 주문 상태 변경 | STORE/ADMIN |
| GET | `/orders/frontend/customer-summaries` | 고객 통계 | STORE/ADMIN |

### service-delivery (`/api/v1/deliveries`)

| Method | 경로 | 설명 | 권한 |
|---|---|---|---|
| GET | `/deliveries` | 배달 목록 | RIDER/ADMIN |
| GET | `/deliveries/{id}` | 배달 상세 | STORE/ADMIN |
| PATCH | `/deliveries/{id}/status` | 배달 상태 변경 | RIDER |

---

## ⚠️ 에러 코드 정책

| HTTP 상태 | 의미 | 예시 |
|---|---|---|
| 200 | 성공 | 정상 처리 |
| 400 | Bad Request | 유효성 검증 실패 |
| 401 | Unauthorized | JWT 없음 / 만료 |
| 403 | Forbidden | 권한 부족 |
| 404 | Not Found | 리소스 없음 |
| 500 | Internal Server Error | 서버 오류 |

> [!tip] 에러 상세 노출 제한
> 클라이언트에는 **일반화된 오류 메시지**만 노출하고,
> 상세 예외 스택 트레이스는 서버 로그에만 기록합니다.

---

## 🔗 연관 문서

- [[04_마이크로서비스]] - 서비스별 역할
- [[09_보안_현황]] - 인가 강화 내역

#API #REST #응답포맷 #인증 #인가
