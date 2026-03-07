# 06. API 설계 및 통신 표준 (API Design Standards)

마이크로서비스 간, 그리고 클라이언트(웹/앱)와의 일관된 통신을 위해 아래 표준을 준수합니다.

## 1. URI 네이밍 규칙
*   **소문자 사용**: 대문자는 사용하지 않습니다.
*   **하이픈(`-`) 사용**: 단어 구분은 언더바(`_`) 대신 하이픈을 사용합니다.
*   **복수형 사용**: 리소스 컬렉션은 복수형 명사를 사용합니다.
*   **계층 구조**: `/api/{version}/{resource}/{id}` 형식을 따릅니다.

**Example:**
*   `GET /api/v1/users` (O)
*   `GET /api/v1/UserList` (X)
*   `GET /api/v1/users/1/orders` (유저 1번의 주문 목록)

## 2. HTTP Method 활용
*   `GET`: 리소스 조회 (Body 사용 금지)
*   `POST`: 리소스 생성 (멱등성 보장 X)
*   `PUT`: 리소스 전체 수정 (멱등성 보장)
*   `PATCH`: 리소스 일부 수정
*   `DELETE`: 리소스 삭제

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

## 4. 내부 통신 (Feign Client)
서비스 간 통신(예: 주문 서비스 -> 가게 서비스)은 Spring Cloud OpenFeign을 사용합니다.
*   **Timeout 설정**: 기본 3초, 최대 5초로 설정하여 연쇄 지연(Cascading Failure) 방지.
*   **Fallback**: 호출 실패 시 기본값을 반환하거나 에러를 던지는 Fallback Factory 구현.
