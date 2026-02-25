# gRPC 내부 통신 설정

이 프로젝트의 모든 MSA 서비스는 내부 통신에 gRPC를 사용합니다.

## 포트 할당

| 서비스 | HTTP 포트 | gRPC 포트 |
|--------|-----------|-----------|
| service-auth | 7000 | 9000 |
| service-user | 8010 | 9010 |
| service-store | 8020 | 9020 |
| service-event | 8030 | 9030 |
| service-order | 8040 | 9040 |
| service-delivery | 8050 | 9050 |
| service-gateway | 8000 | - (클라이언트만) |
| service-discovery | 8100 | - |

## 통신 구조

```
┌─────────────────┐     HTTP/REST      ┌──────────────────┐
│     Client      │ ◄───────────────── │  service-gateway │
└─────────────────┘                    └────────┬─────────┘
                                                │ gRPC
                                                ▼
                         ┌──────────────────────────────────────┐
                         │                                      │
              ┌──────────▼────────┐                             │
              │   service-auth    │◄────── JWT 검증 ────────────┤
              └──────────┬────────┘                             │
                         │ gRPC                                 │
                         ▼                                      │
              ┌──────────────────┐                              │
              │   service-user   │◄─────── 사용자 조회 ─────────┤
              └──────────────────┘                              │
                                                                │
              ┌──────────────────┐                              │
              │   service-store  │◄─────── 매장/상품 조회 ──────┤
              └──────────────────┘                              │
                                                                │
              ┌──────────────────┐                              │
              │   service-order  │◄─────── 주문 관리 ───────────┤
              └──────────────────┘                              │
                         │ gRPC                                 │
                         ▼                                      │
              ┌──────────────────┐                              │
              │  service-delivery│◄─────── 배달 관리 ───────────┤
              └──────────────────┘                              │
                                                                │
              ┌──────────────────┐                              │
              │   service-event  │◄─────── 이벤트/쿠폰 ─────────┘
              └──────────────────┘
```

## gRPC 서비스 정의 (Proto 파일)

각 서비스의 proto 파일은 `src/main/proto/` 디렉토리에 있습니다:

- `service-user/src/main/proto/user.proto` - 사용자 인증/조회
- `service-auth/src/main/proto/auth.proto` - JWT 검증/갱신
- `service-store/src/main/proto/store.proto` - 매장/상품 조회
- `service-order/src/main/proto/order.proto` - 주문 관리
- `service-delivery/src/main/proto/delivery.proto` - 배달 관리
- `service-event/src/main/proto/event.proto` - 이벤트 발행

## 빌드 및 실행

### Proto 코드 생성
```bash
.\gradlew.bat clean generateProto
```

### 전체 빌드
```bash
.\gradlew.bat clean build
```

## gRPC 클라이언트 사용 예시

```java
@Service
public class MyService {
    
    @GrpcClient("service-user")
    private UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userStub;
    
    public void authenticateUser(String email, String password) {
        AuthenticateRequest request = AuthenticateRequest.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .build();
        AuthenticateResponse response = userStub.authenticate(request);
        
        if (response.getSuccess()) {
            // 인증 성공
        }
    }
}
```

## application.yml 설정

### gRPC 서버 (서비스 제공자)
```yaml
grpc:
  server:
    port: 9010
```

### gRPC 클라이언트 (서비스 소비자)
```yaml
grpc:
  client:
    service-user:
      address: static://localhost:9010
      negotiation-type: plaintext
```

## 주의사항

1. **포트 충돌**: HTTP 포트와 gRPC 포트가 겹치지 않도록 주의
2. **Proto 동기화**: 서비스 간 통신 시 proto 파일을 동기화해야 함
3. **보안**: 운영 환경에서는 TLS 설정 필요 (`negotiation-type: tls`)
4. **서비스 디스커버리**: Eureka와 연동 시 `address: discovery:///service-name` 사용 가능
