# service-event

Event 처리용 마이크로서비스입니다.

## 주요 엔드포인트

- `POST /api/v1/events`
- `GET /api/v1/events/{id}`
- `GET /api/v1/info`

## 실행

```bat
cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
.\gradlew.bat :service-event:bootRun
```

## Spring REST Docs 생성

```bat
cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
.\gradlew.bat :service-event:test :service-event:asciidoctor
```

- 스니펫 출력 경로: `service-event\build\generated-snippets`
- Asciidoc HTML 출력 경로(기본): `service-event\build\docs\asciidoc`
