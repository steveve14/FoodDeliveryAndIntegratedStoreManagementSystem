---
tags:
  - Git
  - 컨벤션
  - 브랜치전략
  - 커밋
---

# 12. Git 컨벤션

---

## 🌿 브랜치 전략 (Git Flow)

```
main
├── develop
│   ├── feature/login-auth
│   ├── feature/order-api
│   └── feature/store-layout
└── hotfix/v1.0.1
```

| 브랜치 | 역할 |
|---|---|
| `main` | 제품으로 출시 가능한 상태 (Production) |
| `develop` | 다음 출시 버전 개발 (Staging) |
| `feature/{기능명}` | 기능 개발 (`develop`에서 분기) |
| `hotfix/{버전}` | 출시 버전 버그 수정 (`main`에서 분기) |

### 브랜치 네이밍 예시

```
feature/login-auth
feature/order-api
feature/store-table-layout
feature/android-kiosk-api
hotfix/v1.0.1
```

---

## 📝 커밋 메시지 컨벤션

**Conventional Commits** 규칙을 따릅니다.

```
태그: 제목
```

### 태그 목록

| 태그 | 설명 | 예시 |
|---|---|---|
| `feat` | 새로운 기능 추가 | `feat: 소셜 로그인(카카오) 기능 구현` |
| `fix` | 버그 수정 | `fix: 주문 금액 계산 오류 수정` |
| `docs` | 문서 수정 | `docs: API 명세서 업데이트` |
| `style` | 코드 포맷팅 (코드 변경 없음) | `style: spotless 포맷 적용` |
| `refactor` | 리팩토링 (기능 변경 없음) | `refactor: 주문 서비스 레이어 분리` |
| `test` | 테스트 코드 추가/수정 | `test: 주문 생성 단위 테스트 추가` |
| `chore` | 빌드, 패키지 매니저 설정 등 | `chore: Gradle 의존성 업데이트` |

### 커밋 메시지 작성 규칙

1. 태그 뒤 콜론(`:`) + 공백
2. 제목은 명령형으로 (동사 원형)
3. 50자 이내 권장
4. 마침표 생략

### 커밋 예시

```
feat: 배달 상태 실시간 조회 API 추가
fix: Refresh Token 만료 시 쿠키 미삭제 버그 수정
docs: Obsidian 프로젝트 문서 추가
chore: Spring gRPC 1.0.2 GA 업그레이드
refactor: GrpcClientConfig 생성자 주입으로 전환
test: Playwright E2E 주문 플로우 시나리오 추가
security: 내부 서비스 JWT 재검증 로직 추가
```

---

## 🔀 Pull Request 규칙

```
PR 제목: [태그] 변경 내용 요약
본문:
- 변경 사항: ...
- 테스트 방법: ...
- 체크리스트:
  - [ ] 컴파일 성공
  - [ ] E2E 통과 (해당되는 경우)
  - [ ] 문서 업데이트
```

---

## 🔗 연관 문서

- [[10_진행_상황]] - 실제 작업 이력 참고

#Git #브랜치전략 #커밋컨벤션 #GitFlow
