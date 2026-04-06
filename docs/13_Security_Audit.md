# 보안 감사 보고서

> **작성일**: 2026-03-01
> **최종 수정**: 2026-04-05
> **대상**: 전체 백엔드(MSA) + 프론트엔드(3개 웹 앱, 4개 Android 앱)
> **프로젝트 성격**: 토이 프로젝트 (소스 공개 허용, 금전 관련 없음)

---

## 1. 요약

| 구분 | 상태 |
| --- | --- |
| 핵심 인증/인가 버그 | ✅ 1차 수정 완료 |
| 서비스 직접 접근 시 헤더 스푸핑 방어 | ✅ 서비스 내부 JWT 재검증으로 보강 |
| 쿠키/리프레시 토큰 보안 | ✅ 1차 강화 완료 |
| 운영 배포 전 추가 하드닝 | 📋 남아 있음 |

> 토이 프로젝트 특성상 **저장소 내 실제 시크릿/연결 정보 존재 문제는 수정 대상에서 제외**했습니다.
> 본 문서는 그 외 보안 이슈의 현재 상태만 정리합니다.

---

## 2. 2026-04-05 기준 수정 완료 항목

### 2.1 Gateway 인증 필터 미적용

- 상태: ✅ 완료
- 결과: 인증이 필요한 라우트(user, store, order, delivery, event)에 `AuthorizationHeaderFilter` 적용 유지

### 2.2 Refresh Token role 클레임 누락

- 상태: ✅ 완료
- 결과: refresh 경로에서도 role 클레임이 유지되며 재발급 access token 권한 누락 문제 해소

### 2.3 DTO 유효성 검증 부재

- 상태: ✅ 완료
- 결과: 요청 DTO에 Bean Validation 적용, 전 서비스 Controller/예외 처리기 정렬 완료

### 2.4 에러 핸들러 내부 정보 노출

- 상태: ✅ 완료
- 결과: 클라이언트에는 일반화된 오류 메시지만 노출, 상세 예외는 서버 로그로 제한

### 2.5 Gateway 죽은 경로 분기 제거

- 상태: ✅ 완료
- 결과: 실제 컨트롤러 경로만 유지, 세부 인가는 각 서비스 책임으로 정리

### 2.6 서비스 직접 접근 시 헤더 스푸핑 위험

- 상태: ✅ 1차 완료
- 결과:
  - `service-user`, `service-store`, `service-order`, `service-delivery`, `service-event`가 Authorization 헤더 또는 `access-token` 쿠키에서 JWT를 직접 검증
  - `X-User-Id`, `X-User-Role` 헤더와 토큰 클레임 불일치 시 요청 차단
- 한계: 내부 서비스 포트가 여전히 외부 노출되어 있어 네트워크 레벨 차단은 추가 필요

### 2.7 IDOR 및 민감 엔드포인트 접근제어 누락

- 상태: ✅ 1차 완료
- 결과:
  - `GET /api/v1/orders/{id}`: `USER`, `STORE`, `ADMIN` 권한 + 소유권/역할 검증 적용
  - `GET /api/v1/deliveries/{id}`: `STORE`, `ADMIN` 권한 적용
  - `GET /api/v1/orders/frontend/customer-summaries`: `STORE`, `ADMIN` 권한 적용
  - `GET /api/v1/users/me/addresses/**`: `USER`, `ADMIN` 권한 적용
  - `GET /api/v1/users/frontend/**`: `ADMIN` 권한 적용
  - Event API: 역할 기반 보호 적용

### 2.8 쿠키 보안 속성 미흡

- 상태: ✅ 1차 완료
- 결과:
  - `ResponseCookie` 기반으로 쿠키 발급/삭제 통일
  - `SameSite=Lax` 적용
  - `Secure=true` 기본값, `application-local.yml`에서만 `false`

### 2.9 Refresh Token 평문 저장

- 상태: ✅ 완료
- 결과: DB에는 SHA-256 해시값만 저장, refresh/logout 조회도 해시 기준으로 동작
- 운영 주의: 이전 평문 기준 세션은 재로그인이 필요할 수 있음

### 2.10 내부 서비스 JWT 검증용 시크릿 전달 누락

- 상태: ✅ 완료
- 결과: `docker-compose.yml`에서 `service-user`, `service-store`, `service-event`, `service-order`, `service-delivery`에 `TOKEN_SECRET` 전달

---

## 3. 남아 있는 권장 개선사항

### 3.1 내부 서비스 포트 외부 노출

- 상태: 📋 미완료
- 내용: Docker Compose 기준 내부 서비스 HTTP 포트가 호스트에 공개되어 있음
- 권장: Gateway와 필요한 포트만 외부 노출, 나머지는 internal network로 제한

### 3.2 `customer-summaries` tenant scoping 정교화

- 상태: 📋 미완료
- 내용: 현재 `STORE`, `ADMIN` 권한 제한은 적용됐지만 STORE가 어떤 범위의 고객 요약을 볼 수 있는지 정책이 더 명확해야 함
- 권장: 매장 기준으로 조회 범위를 제한하거나 전용 관리자 API로 분리

### 3.3 Gateway 선행 헤더 제거/재주입 명시화

- 상태: 📋 미완료
- 내용: 서비스 내부 재검증이 추가됐지만, Gateway 레벨에서 기존 `X-User-*` 헤더를 제거 후 재주입하는 정책을 명시적으로 넣으면 방어가 더 단단해짐

### 3.4 gRPC TLS 또는 내부망 보호 정책

- 상태: 📋 미완료
- 내용: gRPC 채널은 현재 개발 편의 중심 설정이며 운영용 암호화/신뢰 경계 설계가 없음

### 3.5 Rate Limiting 미적용

- 상태: 📋 미완료
- 권장: 로그인, 회원가입, 토큰 갱신 등에 `RequestRateLimiter` 또는 `bucket4j` 적용

### 3.6 비밀번호 정책/계정 잠금/감사 로깅

- 상태: 📋 미완료
- 권장:
  - 비밀번호 복잡도 검증 추가
  - 로그인 실패 횟수 누적 및 잠금 정책 추가
  - 로그인, 주문 상태 변경, 배달 상태 변경 감사 로그 추가

### 3.7 보안 헤더 및 프론트 운영 설정

- 상태: 📋 미완료
- 권장:
  - Nuxt 보안 헤더(CSP, HSTS, X-Frame-Options 등) 적용
  - Android `usesCleartextTraffic` 운영 분리
  - Android API Base URL `BuildConfig`/flavor 분리

---

## 4. 토이 프로젝트 기준 허용 항목

| 항목 | 상태 | 비고 |
| --- | --- | --- |
| 저장소 내 시크릿/연결 정보 | 허용 | 사용자 요청으로 수정 대상 제외 |
| DB 기본 비밀번호/예제 값 | 허용 | 실무 전환 시 즉시 제거 필요 |
| 개발용 localhost/gRPC 주소 | 조건부 허용 | 운영 전 환경 분리 필요 |

---

## 5. 양호 항목

| 항목 | 상태 | 비고 |
| --- | --- | --- |
| SQL Injection | ✅ 안전 | Spring Data/JdbcTemplate 파라미터 기반 사용 |
| 비밀번호 해싱 | ✅ 양호 | BCryptPasswordEncoder 사용 |
| XSS (`v-html`) | ✅ 안전 | 프론트엔드에서 `v-html` 미사용 |
| localStorage 토큰 저장 | ✅ 안전 | 사용하지 않음 |
| Refresh Token Rotation | ✅ 구현됨 | 토큰 갱신 시 이전 토큰 폐기 |
| 서비스 내부 재검증 | ✅ 추가됨 | 헤더 단독 신뢰 구조 제거 |

---

## 6. 후속 로드맵

### 즉시

- Docker Compose 내부 서비스 포트 비공개화
- `customer-summaries` tenant scoping 명확화
- Gateway 선행 헤더 제거/재주입 정책 반영

### 운영 배포 전

- gRPC TLS 또는 내부망 보호 정책
- Rate Limiting
- 보안 헤더/Android 네트워크 정책 정리
- 감사 로깅과 계정 잠금 정책 도입

---
