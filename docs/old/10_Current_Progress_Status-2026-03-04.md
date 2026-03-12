# 10. 현재 진행 상황 정리 (2026-02-25)

## 1) 요약
- 현재 저장소는 **MSA 인프라 골격 + 일부 인증/회원 기능 + 프론트 템플릿 기반 화면**까지 진행된 상태입니다.
- `docs`의 목표 아키텍처/기능 명세 대비, 핵심 도메인 기능(주문/매장/POS/결제/라이더) 구현은 대부분 초기 단계입니다.

## 2) 완료/구축된 항목

### 백엔드
- 멀티모듈 기반 서비스 구조 구성
  - `service-gateway`, `service-discovery`, `service-auth`, `service-user`, `service-store`, `service-order`, `service-delivery`, `service-event`
- Eureka 기반 서비스 등록/조회 설정 존재
- Gateway 라우팅 설정 존재
- Auth 서비스 로그인/소셜 로그인 엔드포인트 일부 구현
- User 서비스 회원 등록/조회 API 일부 구현

### 프론트엔드
- Nuxt 기반 3개 앱 구조 존재
  - `web-admin`, `web-shop`, `web-user`
- `web-admin`, `web-shop`은 Nuxt Dashboard 템플릿 기반 UI 구성
- `web-user`는 메인 페이지 중심의 초기 UI 구성

## 3) 진행 중/초기 상태 항목
- 서비스별 실제 비즈니스 API(주문 상태 전이, 매장/메뉴/재고, POS 정산 등)는 본격 구현 전 단계
- 서비스 간 내부 통신 표준(Feign + timeout/fallback) 적용 코드 미확인
- 공통 응답 포맷(ApiResponse) 및 API 버저닝(`/api/v1`) 규칙 적용 미흡

## 4) 문서 대비 주요 차이점

### 아키텍처/서비스 구성
- 문서 상 `Config`, `Payment`, `Rider` 축이 중요하게 정의되어 있으나 현재 백엔드 디렉토리(`service-auth`, `service-user`, `service-store`, `service-order`, `service-delivery`, `service-event`, `service-gateway`, `service-discovery`)에 직접 대응 모듈이 없음
- `service-event`, `service-delivery`는 존재하나 문서의 서비스 명/역할 체계와 1:1 대응이 불명확

### API 표준
- 문서 표준: `/api/v1/{resource}` + `ApiResponse<T>` 래퍼
- 현재 코드: `/api/users`, `/api/auth`, `/api/info` 등 혼재 및 래퍼 일관성 미적용

### DB/기술스택
- 문서: Local SQLite / Prod MySQL 중심 전략
- 현재: PostgreSQL 중심 datasource 설정
- 루트 빌드 설정에서 Spring Boot 버전이 문서 기술스택과 차이

### 프론트 제품 구조
- 문서: `web-dashboard`, `tablet-pos` 등 역할 기반 구조
- 현재 디렉토리: `web-admin`, `web-shop`, `web-user` 구조(템플릿 성격이 강함)

## 5) 리스크
- 문서와 코드 간 불일치 누적으로 신규 인력 온보딩/개발 우선순위 혼선 위험
- API 규약 미통일 시 프론트-백엔드 연동 비용 증가
- 서비스 간 통신 표준 부재 시 장애 전파/지연 대응 취약

## 6) 다음 액션(권장)
1. "문서를 코드에 맞출지" 또는 "코드를 문서 목표로 끌어올릴지" 기준 먼저 확정
2. API 표준(버전/응답 포맷/에러 모델) 선적용
3. 서비스 경계와 책임 재정의 후 P0 백로그 실행
