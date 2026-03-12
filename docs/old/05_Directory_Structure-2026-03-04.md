# 05. 프로젝트 폴더 구조 (Directory Structure)

이 프로젝트는 모노레포(Monorepo) 스타일로 구성되어 있습니다.

## 1. 최상위 구조
```text
ProjectRoot/
├── backend/       # Spring Boot 마이크로서비스 집합
├── frontend/      # Nuxt.js 웹 프로젝트 집합
├── docs/          # 프로젝트 문서
└── README.md
```

## 2. Backend 구조 (`/backend`)
현재 백엔드는 `backend/msa-root` 멀티모듈 구조입니다.

*   `msa-root/service-discovery/`: Eureka Server (서비스 등록/발견)
*   `msa-root/service-gateway/`: Spring Cloud Gateway (모든 요청의 진입점)
*   `msa-root/service-auth/`: 유저 인증 및 토큰 발급
*   `msa-root/service-user/`: 유저 정보/프로필 도메인
*   `msa-root/service-store/`: 가게, 메뉴, 테이블 관리 로직
*   `msa-root/service-order/`: 주문 생성 및 처리 로직
*   `msa-root/service-delivery/`: 배달 도메인
*   `msa-root/service-event/`: 이벤트/마케팅 도메인

## 3. Frontend 구조 (`/frontend`)
Nuxt.js 기반의 웹 애플리케이션들입니다.

*   `web-user/`: 일반 고객이 웹 브라우저로 접속하는 배달 사이트
*   `web-shop/`: 사장님/매장 운영용 웹 앱
*   `web-admin/`: 관리자 운영 웹 앱

## 4. Mobile 구조 (`/mobile`)
*   현재 저장소 기준 별도 `mobile/` 디렉토리는 없습니다. (추후 분리/추가 예정)
