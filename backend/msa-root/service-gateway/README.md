# service-gateway

API Gateway 서비스입니다.

## 주요 엔드포인트

- `GET /api/v1/info`

## 역할

- 클라이언트 요청 진입점
- JWT 검증 및 `X-User-Id`, `X-User-Role` 헤더 주입
- 하위 마이크로서비스 라우팅

## 실행

```bat
cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
set TOKEN_SECRET=<BASE64_SECRET>
.\gradlew.bat :service-gateway:bootRun
```

- `TOKEN_SECRET` 환경 변수가 비어 있으면 게이트웨이는 시작하지 않습니다.

## Spring REST Docs 생성

```bat
cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
.\gradlew.bat :service-gateway:test :service-gateway:asciidoctor
```

- 스니펫 출력 경로: `service-gateway\build\generated-snippets`
- Asciidoc HTML 출력 경로(기본): `service-gateway\build\docs\asciidoc`
