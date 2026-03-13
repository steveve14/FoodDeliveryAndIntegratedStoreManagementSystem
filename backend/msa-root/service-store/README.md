# service-store

가게 및 메뉴 관리용 마이크로서비스입니다.

## 주요 엔드포인트

- `POST /api/v1/stores`
- `GET /api/v1/stores`
- `GET /api/v1/stores/me`
- `GET /api/v1/stores/{id}`
- `GET /api/v1/stores/{storeId}/menus`

## 실행

```bat
cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
.\gradlew.bat :service-store:bootRun
```

## Spring REST Docs 생성

```bat
cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
.\gradlew.bat :service-store:test :service-store:asciidoctor
```

- 스니펫 출력 경로: `service-store\build\generated-snippets`
- Asciidoc HTML 출력 경로(기본): `service-store\build\docs\asciidoc`
