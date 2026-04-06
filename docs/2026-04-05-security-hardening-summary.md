# 2026-04-05 Security Hardening Summary

> 작성일: 2026-04-05
> 범위: backend/msa-root 보안 하드닝 1차 반영 결과 요약

---

## 1. 이번 반영 요약

### 1.1 서비스 내부 JWT 재검증 추가

- 대상 서비스
  - `service-user`
  - `service-store`
  - `service-order`
  - `service-delivery`
  - `service-event`
- 변경 내용
  - Authorization 헤더 또는 `access-token` 쿠키에서 JWT를 직접 검증
  - `X-User-Id`, `X-User-Role` 헤더와 토큰 클레임이 다르면 요청 차단
- 목적
  - Gateway 우회 또는 헤더 스푸핑 시도에 대한 1차 방어

### 1.2 민감 엔드포인트 인가 강화

- `GET /api/v1/orders/{id}`
  - `USER`, `STORE`, `ADMIN` 권한 제한
  - 주문 조회 시 소유권/역할 기반 접근 검증 추가
- `GET /api/v1/deliveries/{id}`
  - `STORE`, `ADMIN` 권한 제한
- `GET /api/v1/orders/frontend/customer-summaries`
  - `STORE`, `ADMIN` 권한 제한
- `GET /api/v1/users/me/addresses/**`
  - `USER`, `ADMIN` 권한 제한
- `GET /api/v1/users/frontend/**`
  - `ADMIN` 권한 제한
- `service-event`
  - Event API 역할 기반 접근 제어 추가

### 1.3 Auth 보안 설정 강화

- 쿠키 발급/삭제를 `ResponseCookie` 기준으로 통일
- 기본 설정
  - `SameSite=Lax`
  - `Secure=true`
- 로컬 개발 프로파일
  - `Secure=false`
- Refresh Token 저장 방식
  - 평문 저장 제거
  - SHA-256 해시 저장으로 전환

### 1.4 환경 설정 정렬

- `docker-compose.yml`에 내부 서비스 JWT 검증용 `TOKEN_SECRET` 전달 추가
- 변경 서비스 컴파일 검증 완료
  - `service-auth`
  - `service-user`
  - `service-store`
  - `service-order`
  - `service-delivery`
  - `service-event`

---

## 2. 배포/운영 영향

### 2.1 세션 영향

- Refresh Token 저장 방식이 바뀌었기 때문에, 기존 평문 기준 세션은 재로그인이 필요할 수 있음

### 2.2 환경 변수 영향

- 이제 auth 서비스뿐 아니라 내부 서비스도 `TOKEN_SECRET`가 필요함
- 누락 시 서비스 내부 JWT 재검증이 실패하여 인증 요청이 차단될 수 있음

### 2.3 로컬 개발 영향

- `application-local.yml`에서만 쿠키 `Secure=false`
- HTTPS가 아닌 로컬 개발 환경에서는 local 프로파일 사용 전제가 필요함

---

## 3. 아직 남은 후속 과제

### 3.1 단기 후속

- `customer-summaries`의 STORE 범위 tenant scoping 정교화
- Docker Compose 내부 서비스 포트 외부 노출 제거
- Gateway 선행 `X-User-*` 헤더 제거 후 재주입 정책 명시화

### 3.2 운영 배포 전

- gRPC TLS 또는 내부망 보호 정책 수립
- Redis 비밀번호, CORS 제한, 보안 헤더 정리
- Android Cleartext/API URL 환경 분리

---

## 4. 비고

- 저장소 내 실제 시크릿/연결 정보는 사용자 요청에 따라 이번 수정 범위에서 제외함
- 본 문서는 코드 반영 이후 상태 요약이며, 상세 배경은 `13_Security_Audit.md`와 `15_Security_Analysis_2026-03-29.md`를 참고
