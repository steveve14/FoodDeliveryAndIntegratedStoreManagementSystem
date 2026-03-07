# 보안 감사 보고서

> **작성일**: 2026-03-01  
> **대상**: 전체 백엔드(MSA) + 프론트엔드(3개 앱)  
> **프로젝트 성격**: 토이 프로젝트 (소스 공개 허용, 금전 관련 없음)

---

## 1. 요약

| 심각도 | 건수 | 조치 상태 |
|--------|------|-----------|
| 🔴 기능 버그(치명적) | 4건 | ✅ 수정 완료 |
| 🟡 권장 개선사항 | 6건 | 📋 문서화 |
| 🟢 양호 | 6건 | — |

> 토이 프로젝트 특성상 **하드코딩된 시크릿, DB 비밀번호 기본값** 등은 허용하는 것으로 판단.  
> **기능이 깨진 버그**와 **포트폴리오 품질에 영향을 주는 항목**만 수정 대상으로 분류.

---

## 2. 수정 완료 항목

### 2.1 Gateway 인증 필터 미적용 (기능 버그)

**문제**: `AuthorizationHeaderFilter`가 정의되어 있으나 `service-gateway/application.yml`의 라우트에 적용되지 않아 **모든 API가 무인증 접근 가능**한 상태.

**수정**: 인증이 필요한 라우트(user, store, order, delivery, event)에 `AuthorizationHeaderFilter` 적용. 인증 서비스(`/auth/**`, `/login/**`, `/oauth2/**`)는 필터 제외.

**파일**: `service-gateway/src/main/resources/application.yml`

### 2.2 Refresh Token role 클레임 누락 (기능 버그)

**문제**: `JwtProvider.createRefreshToken()`이 `role` 클레임을 포함하지 않아, 토큰 갱신(refresh) 시 `getRolesFromToken()`이 빈 문자열을 반환 → 새 Access Token에 role이 빠짐.

**수정**:
- `JwtProvider.createRefreshToken()`에 `role` 파라미터 추가 후 `claim("role", role)` 포함
- `AuthService.login()`, `AuthService.verifyGoogleTokenAndLogin()` 등 호출부 수정
- `AuthGrpcServiceImpl.refreshToken()`의 호출부도 수정

**파일**: `JwtProvider.java`, `AuthService.java`, `AuthGrpcServiceImpl.java`

### 2.3 DTO 유효성 검증 부재 (기능 버그)

**문제**: 모든 DTO에 Bean Validation 어노테이션 없음. Controller에 `@Valid` 미사용. → null/빈 값 요청 시 500 에러 발생.

**수정**:
- `build.gradle`(공통)에 `spring-boot-starter-validation` 의존성 추가
- 모든 요청 DTO에 `@NotBlank`, `@NotNull`, `@Size`, `@Email`, `@Min`, `@NotEmpty` 추가
- 모든 Controller에 `@Valid` 추가
- 각 서비스의 `RestExceptionHandler`에 `MethodArgumentNotValidException` 핸들러 추가

**대상 DTO**:
| 서비스 | DTO | 추가된 검증 |
|--------|-----|-------------|
| service-auth | `LoginRequest` | `@NotBlank` email, `@Email` email, `@NotBlank` password |
| service-auth | `GoogleLoginRequest` | `@NotBlank` idToken |
| service-auth | `RefreshRequest` | `@NotBlank` refreshToken |
| service-auth | `LogoutRequest` | `@NotBlank` refreshToken |
| service-user | `CreateUserRequest` | `@NotBlank` email/password/name, `@Email` email, `@Size(min=8)` password |
| service-user | `AuthRequest` | `@NotBlank` email/password, `@Email` email |
| service-order | `CreateOrderRequest` | `@NotBlank` userId/address, `@NotEmpty` items, `@Min(0)` totalAmount |
| service-order | `OrderItemDto` | `@NotBlank` productId, `@Min(1)` quantity, `@Min(0)` price |
| service-store | `CreateStoreRequest` | `@NotBlank` name/address/ownerId |
| service-store | `CreateProductRequest` | `@NotBlank` name, `@Min(0)` price |
| service-event | `CreateEventRequest` | `@NotBlank` type |
| service-delivery | `CreateDeliveryRequest` | `@NotBlank` orderId |

### 2.4 에러 핸들러 내부 정보 노출 수정

**문제**: 6개 서비스의 `handleGeneric(Exception ex)`가 `ex.getMessage()`를 클라이언트에 그대로 반환 → DB 스키마, 내부 클래스명 등 유출 가능.

**수정**: `handleGeneric()`에서 고정 메시지 `"서버 오류가 발생했습니다."` 반환하고, 원본 에러는 `log.error()`로만 기록. (service-user는 이미 양호하여 변경 없음)

**파일**: 5개 서비스의 `RestExceptionHandler.java` (auth, order, delivery, store, event) + gateway

---

## 3. 권장 개선사항 (미수정 — 토이 프로젝트 기준 허용)

### 3.1 JWT 시크릿 하드코딩
- `application.yml`에 Base64 시크릿이 직접 커밋됨
- 토이 프로젝트이므로 허용. 실무에서는 `${TOKEN_SECRET}` 환경변수 치환 필수

### 3.2 DB 비밀번호 기본값
- `${DB_PASSWORD:password}` 폴백 존재
- `.env.example`에 `DB_PASSWORD=1234` 포함
- 토이 프로젝트이므로 허용. 실무에서는 기본값 제거

### 3.3 RBAC(역할 기반 접근 제어) 미구현
- `@PreAuthorize`, `@Secured` 등 미사용
- Gateway가 `X-User-Id`만 전달, role 미전달
- 추후 기능 개발 시 구현 권장

### 3.4 CORS 설정
- 백엔드: CORS 설정 전무
- 프론트엔드: `cors: true` (와일드카드)
- 개발 환경에서는 허용. 배포 시 오리진 제한 필요

### 3.5 Rate Limiting 미적용
- 로그인 무차별 대입 공격 방어 없음
- 추후 `RequestRateLimiter` 또는 `bucket4j` 도입 권장

### 3.6 보안 헤더 미설정
- CSP, HSTS, X-Frame-Options 등 미설정
- 프론트엔드 배포 시 `nuxt.config.ts`에 추가 권장

---

## 4. 양호 항목

| 항목 | 상태 | 비고 |
|------|------|------|
| SQL Injection | ✅ 안전 | Spring Data 메서드명 기반 쿼리만 사용 |
| 비밀번호 해싱 | ✅ 양호 | BCryptPasswordEncoder 사용 |
| XSS (v-html) | ✅ 안전 | 프론트엔드에서 v-html 미사용 |
| localStorage 토큰 저장 | ✅ 안전 | 사용하지 않음 |
| .env Git 추적 | ✅ 안전 | .gitignore로 제외됨 |
| Refresh Token Rotation | ✅ 구현됨 | 토큰 갱신 시 이전 토큰 폐기 |

---

## 5. 의존성 현황

| 라이브러리 | 현재 버전 | 비고 |
|------------|-----------|------|
| Spring Boot | 4.0.2 | 최신 |
| JJWT | 0.11.5 | 최신 0.12.x (deprecated API 사용 중이나 동작에 문제 없음) |
| gRPC | 1.62.2 | 최신 1.66.x+ |
| Nuxt | ^4.2.2 | 최신 |
