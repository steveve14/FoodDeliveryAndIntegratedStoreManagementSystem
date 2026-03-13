# MSA API Documentation Index

이 문서는 각 서비스에서 Spring REST Docs로 생성한 API 문서의 진입점입니다.

## 생성 명령

```bat
cd /d c:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
gradlew.bat docsAggregate
```

## 서비스별 문서 링크

- [Gateway](../service-gateway/build/docs/asciidoc/index.html)
- [Auth](../service-auth/build/docs/asciidoc/index.html)
- [User](../service-user/build/docs/asciidoc/index.html)
- [Store](../service-store/build/docs/asciidoc/index.html)
- [Order](../service-order/build/docs/asciidoc/index.html)
- [Delivery](../service-delivery/build/docs/asciidoc/index.html)
- [Event](../service-event/build/docs/asciidoc/index.html)

## 비고

- `service-discovery`는 현재 `/api/v1/info` 정도의 최소 엔드포인트만 제공하므로 별도 REST Docs 대상에서는 제외했습니다.
- Gateway를 통해 프록시되는 비즈니스 API의 상세 스펙은 각 서비스 문서를 기준으로 봐야 합니다.
- 기존의 수동 문서인 `API_DOCUMENTATION.md`는 아키텍처/흐름 설명용으로 병행 유지할 수 있습니다.
