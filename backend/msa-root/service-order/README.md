# service-order

Order 처리용 마이크로서비스입니다.

## 주요 엔드포인트

- `POST /api/v1/orders`
- `GET /api/v1/orders/{id}`
- `GET /api/v1/orders/my`
- `GET /api/v1/orders/frontend/customer-summaries`
- `GET /api/v1/orders/store/{storeId}`
- `PATCH /api/v1/orders/{id}/status`

## 실행

```bat
cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
.\gradlew.bat :service-order:bootRun
```

## Spring REST Docs 생성

```bat
cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
.\gradlew.bat :service-order:test :service-order:asciidoctor
```

- 스니펫 출력 경로: `service-order\build\generated-snippets`
- Asciidoc HTML 출력 경로(기본): `service-order\build\docs\asciidoc`
