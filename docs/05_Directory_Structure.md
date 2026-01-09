# 05. 프로젝트 폴더 구조 (Directory Structure)

이 프로젝트는 모노레포(Monorepo) 스타일로 구성되어 있습니다.

## 1. 최상위 구조
```text
ProjectRoot/
├── backend/       # Spring Boot 마이크로서비스 집합
├── frontend/      # Nuxt.js 웹 프로젝트 집합
├── mobile/        # Android 네이티브 앱 프로젝트
├── docs/          # 프로젝트 문서
└── README.md
```

## 2. Backend 구조 (`/backend`)
각 폴더는 독립적인 Spring Boot 프로젝트입니다.

*   `discovery-service/`: Eureka Server (서비스 등록/발견)
*   `api-gateway/`: Spring Cloud Gateway (모든 요청의 진입점)
*   `auth-service/`: 유저 인증 및 토큰 발급
*   `store-service/`: 가게, 메뉴, 테이블 관리 로직
*   `order-service/`: 주문 생성 및 처리 로직
*   `payment-service/`: 외부 PG사 연동

## 3. Frontend 구조 (`/frontend`)
Nuxt.js 기반의 웹 애플리케이션들입니다.

*   `web-customer/`: 일반 고객이 웹 브라우저로 접속하는 배달 사이트
*   `web-dashboard/`: 사장님 및 관리자가 접속하는 관리 페이지 (PC 최적화)
    *   `pages/admin/`: 관리자 전용
    *   `pages/owner/`: 사장님 전용
*   `tablet-pos/`: 매장 내 비치된 태블릿용 웹 앱 (터치 인터페이스 최적화)

## 4. Mobile 구조 (`/mobile`)
*   `app-customer/`: 일반 고객용 안드로이드 앱 (Kotlin)
*   `app-rider/`: 배달 기사용 안드로이드 앱 (Kotlin)
