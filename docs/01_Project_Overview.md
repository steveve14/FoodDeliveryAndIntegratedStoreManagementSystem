# 01. 프로젝트 개요 및 R&R

## 1. 프로젝트 목표

음식 배달 앱과 매장 내 POS 시스템을 통합하여, 사장님이 단일 대시보드에서 온/오프라인 주문을 한 번에 관리할 수 있는 플랫폼을 구축합니다. 최신 기술 스택(Spring Boot MSA, Nuxt.js)을 도입하여 확장성과 유지보수성을 확보합니다.

## 2. 프로젝트 일정 (Milestone)

- **Phase 1 (기획/설계)**: 요구사항 분석, MSA 아키텍처 설계, DB 모델링
- **Phase 2 (백엔드 코어)**: Eureka, Gateway 구축, 공통 모듈, 인증 서비스 개발
- **Phase 3 (비즈니스 로직)**: 주문, 가게, 결제 마이크로서비스 개발
- **Phase 4 (프론트엔드)**: 사장님 대시보드(Web), 매장 태블릿(Web), 고객 앱(App) 구현
- **Phase 5 (통합/배포)**: 서비스 연동 테스트, CI/CD 구축

## 3. 역할과 책임 (R&R)

### 3.1. 백엔드 개발 (Spring Boot)

- **MSA 환경 구축**: `service-discovery`, `service-gateway` 및 `service-*` 모듈 설정/관리.
- **API 개발**: 도메인별(주문, 가게, 유저) 독립적인 REST API 서버 개발.
- **데이터 설계**: Spring Data JDBC를 활용한 DB 접근 계층 구성. 로컬(H2 in-memory) / 운영(PostgreSQL) 환경 분리.
- **내부 통신**: **Spring gRPC**(공식 Spring 프로젝트, 1.0.0-RC1)를 이용한 서비스 간 gRPC 통신 구현. (Protobuf 스키마 공유, 서비스별 GrpcClientConfig 빈 등록)
- **인증**: jjwt 0.12.x (Jakarta EE 호환) 기반 JWT Access/Refresh Token 발급 및 검증.

### 3.2. 프론트엔드 개발 (Nuxt.js)

- **웹 애플리케이션**: 고객용 웹(`web-customer`), 사장님 대시보드(`web-shop`), 관리자 시스템(`web-admin`) 개발.
- **UI/UX**: 재사용 가능한 Vue 3 컴포넌트 설계 및 반응형 레이아웃 구현.
- **상태 관리**: composable 패턴(`useApi`, `useAuth`) 및 Pinia를 활용한 전역 상태 관리.
- **패키지 관리**: pnpm 기반 워크스페이스.

### 3.3. 모바일 개발 (Android)

- 현재 저장소에 `mobile/` 디렉토리 미포함. 추후 별도 리포지토리 또는 서브모듈로 분리 예정.
- 계획: 위치 기반 배달 라이더 앱, 고객 앱 (웹뷰 하이브리드 검토 중).
