# 06. API 설계 및 통신 표준 (API Design Standards)

> **최종 수정**: 2026-04-05

마이크로서비스 간, 그리고 클라이언트(웹/앱)와의 일관된 통신을 위해 아래 표준을 준수합니다.

## 1. URI 네이밍 규칙

* **소문자 사용**: 대문자는 사용하지 않습니다.
* **하이픈(`-`) 사용**: 단어 구분은 언더바(`_`) 대신 하이픈을 사용합니다.
* **복수형 사용**: 리소스 컬렉션은 복수형 명사를 사용합니다.
* **계층 구조**: `/api/{version}/{resource}/{id}` 형식을 따릅니다.

**Example:**

* `GET /api/v1/users` (O)
* `GET /api/v1/UserList` (X)
* `GET /api/v1/users/1/orders` (유저 1번의 주문 목록)

## 2. HTTP Method 활용

* `GET`: 리소스 조회 (Body 사용 금지)
* `POST`: 리소스 생성 (멱등성 보장 X)
* `PUT`: 리소스 전체 수정 (멱등성 보장)
* `PATCH`: 리소스 일부 수정
* `DELETE`: 리소스 삭제

## 3. 공통 응답 포맷 (Global Response Wrapper)

모든 API 응답은 아래 `ApiResponse<T>` 객체로 감싸서 반환합니다. 이를 통해 프론트엔드에서 예외 처리를 통일할 수 있습니다.

```json
{
  "success": true,
  "code": 200,
  "message": "요청이 성공했습니다.",
  "data": {
    "orderId": 123,
    "totalPrice": 15000,
    "status": "COOKING"
  }
}
```

### 에러 응답 예시

```json
{
  "success": false,
  "code": 400,
  "message": "재고가 부족하여 주문할 수 없습니다.",
  "data": null
}
```

### 예외 정책 (Binary/File Response)

파일/바이너리 전송 엔드포인트는 `ApiResponse<T>` 래퍼를 사용하지 않습니다.

* 적용 대상: `GET /api/v1/users/avatars/{filename}`
* 응답 방식: `ResponseEntity<Resource>` (콘텐츠 타입 + 캐시 헤더 포함)
* 에러 방식: 파일 미존재 등 오류는 공통 예외 핸들러 정책에 따라 HTTP 상태코드로 반환

## 4. 인증/인가 표준

### 4.1 기본 인증 방식

- 외부 클라이언트는 Gateway를 통해서만 접근합니다.
- 인증 정보는 httpOnly 쿠키(`access-token`, `refresh-token`)를 기본으로 사용합니다.
- 필요 시 `Authorization: Bearer <accessToken>` 헤더도 허용합니다.

### 4.2 신뢰 경계

- Gateway가 `X-User-Id`, `X-User-Role` 헤더를 전달하더라도, 내부 서비스는 해당 헤더만 단독 신뢰하지 않습니다.
- 내부 서비스는 Authorization 헤더 또는 `access-token` 쿠키의 JWT를 직접 검증하고, 전달 헤더와 토큰 클레임 일치 여부를 확인해야 합니다.
- `TOKEN_SECRET`는 Gateway뿐 아니라 JWT를 검증하는 내부 서비스에도 동일하게 주입되어야 합니다.

### 4.3 민감 엔드포인트 규칙

- 단건 조회 API는 역할 검증만으로 끝내지 않고 소유권 또는 테넌트 범위 검증까지 수행합니다.
- 예시:
  - 주문 상세: 본인 사용자, 해당 매장, 관리자만 접근 가능
  - 배송 상세: 매장 또는 관리자만 접근 가능
  - 관리자/운영 집계 API: 일반 사용자 경로와 분리하고 ADMIN 또는 별도 운영 역할만 허용

## 5. 내부 통신 (Spring gRPC)

서비스 간 동기 통신(예: 주문 서비스 → 가게 서비스)은 **Spring gRPC (Protobuf 스키마 기반)**을 사용합니다.

> **이전 전략(Feign Client)에서 변경**: OpenFeign 의존성은 제거되었으며, 모든 서비스 간 동기 호출은 gRPC로 수행합니다.
> Spring gRPC 1.0.2 GA (Maven Central, Spring Boot 4.0.2 정식 지원)

### 서비스 간 gRPC 호출 구조

| 호출자 | 대상 | Proto RPC | 용도 |
| --- | --- | --- | --- |
| service-gateway | service-auth | `ValidateToken` | JWT 토큰 검증 |
| service-auth | service-user | `Authenticate` / `GetUserByEmail` | 사용자 조회 |
| service-order | service-store | `GetProductById` | 상품 정보 조회 |
| service-delivery | service-order | `GetOrderById` | 주문 정보 조회 |

### 클라이언트 구현 방식

각 gRPC 클라이언트 서비스는 `GrpcClientConfig.java`에서 `GrpcChannelFactory`를 이용해 `BlockingStub` 빈을 등록합니다.

```java
@Configuration
public class GrpcClientConfig {
    @Bean
    StoreGrpcServiceGrpc.StoreGrpcServiceBlockingStub storeGrpcStub(GrpcChannelFactory channels) {
        return StoreGrpcServiceGrpc.newBlockingStub(channels.createChannel("service-store"));
    }
}
```

채널 주소는 `application.yml`에서 설정합니다:

```yaml
spring:
  grpc:
    client:
      channels:
        service-store:
          address: static://localhost:9020
```

---