# 05. 프로젝트 폴더 구조 (Directory Structure)

> **최종 수정**: 2026-03-04

이 프로젝트는 모노레포(Monorepo) 스타일로 구성되어 있습니다.

## 1. 최상위 구조
```text
ProjectRoot/
├── backend/       # Spring Boot 마이크로서비스 집합
├── frontend/      # Nuxt.js 웹 프로젝트 집합
├── docs/          # 프로젝트 문서 (old/ 에 이전 버전 보관)
└── README.md
```

## 2. Backend 구조 (`/backend`)
현재 백엔드는 `backend/msa-root` 멀티모듈 Gradle 구조입니다.

*   `msa-root/service-discovery/`: Eureka Server (서비스 등록/발견)
*   `msa-root/service-gateway/`: Spring Cloud Gateway (모든 요청의 진입점, JWT 필터)
*   `msa-root/service-auth/`: 유저 인증 및 토큰 발급
*   `msa-root/service-user/`: 유저 정보/프로필 도메인
*   `msa-root/service-store/`: 가게, 메뉴(상품), 테이블 관리 로직
*   `msa-root/service-order/`: 주문 생성 및 처리 로직
*   `msa-root/service-delivery/`: 배달 도메인
*   `msa-root/service-event/`: 이벤트/마케팅 도메인

### 서비스 내부 주요 패키지 (예: service-auth)
```text
service-auth/src/main/java/com/example/auth/
├── config/
│   └── GrpcClientConfig.java   # GrpcChannelFactory로 BlockingStub 빈 등록
├── grpc/
│   └── AuthGrpcServiceImpl.java  # @GrpcService 구현체
├── security/
│   └── JwtProvider.java          # jjwt 0.12.6 기반 JWT 생성/검증
└── ...
```

> gRPC 클라이언트를 사용하는 서비스(auth, gateway, order, delivery)는 각자 `config/GrpcClientConfig.java`를 보유하며,  
> `GrpcChannelFactory`를 통해 `BlockingStub` 빈을 등록합니다.

### gRPC Protobuf 스키마 위치
각 서비스의 `src/main/proto/` 하위에 `.proto` 파일 위치. 빌드 시 자동으로 Java 소스 생성.

| 서비스 | proto 파일 |
|---|---|
| service-auth | `auth.proto` |
| service-user | `user.proto` |
| service-store | `store.proto` |
| service-order | `order.proto` |

## 3. Frontend 구조 (`/frontend`)
Nuxt.js 4.x 기반의 웹 애플리케이션들입니다. 패키지 관리자는 **pnpm**을 사용합니다.

*   `web-customer/`: 일반 고객이 웹 브라우저로 접속하는 배달 사이트
*   `web-shop/`: 사장님/매장 운영용 웹 앱 (POS 겸용)
*   `web-admin/`: 시스템 관리자 운영 웹 앱

```text
frontend/
├── web-admin/
│   ├── app/
│   │   ├── components/
│   │   ├── composables/     # useApi, useAuth 등
│   │   ├── pages/
│   │   └── ...
│   ├── nuxt.config.ts
│   └── package.json
├── web-shop/
│   └── (web-admin과 유사 구조)
└── web-customer/
    └── (Pinia 상태 관리 포함)
```

## 4. Mobile 구조 (`/mobile`)
*   현재 저장소 기준 별도 `mobile/` 디렉토리는 없습니다. (추후 분리/추가 예정)

## 5. 문서 (`/docs`)
*   `docs/`: 최신 버전 문서
*   `docs/old/`: 이전 버전 문서 (`파일명-YYYY-MM-DD.md` 형식으로 보관)

---