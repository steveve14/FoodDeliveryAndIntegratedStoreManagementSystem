# service-delivery

배달 관리용 마이크로서비스입니다.

## 주요 엔드포인트

- `POST /api/v1/deliveries`
- `GET /api/v1/deliveries/{id}`
- `PATCH /api/v1/deliveries/{id}/status`
- `GET /api/v1/info`

## 실행

```bat
cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
.\gradlew.bat :service-delivery:bootRun
```

## Spring REST Docs 생성

```bat
cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
.\gradlew.bat :service-delivery:test :service-delivery:asciidoctor
```

- 스니펫 출력 경로: `service-delivery\build\generated-snippets`
- Asciidoc HTML 출력 경로(기본): `service-delivery\build\docs\asciidoc`
