# 09. 깃 브랜치 전략 및 커밋 컨벤션 (Git Strategy)

## 1. Branch Strategy (Git Flow)
*   **main**: 제품으로 출시될 수 있는 브랜치 (Production)
*   **develop**: 다음 출시 버전을 개발하는 브랜치 (Staging)
*   **feature/{기능명}**: 기능을 개발하는 브랜치 (`develop`에서 분기)
    *   예: `feature/login-auth`, `feature/order-api`
*   **hotfix/{버전}**: 출시 버전에서 발생한 버그 수정

## 2. Commit Message Convention
협업을 위해 **Conventional Commits** 규칙을 따릅니다.

`태그: 제목` 형태를 유지합니다.

| 태그 | 설명 |
|---|---|
| **feat** | 새로운 기능 추가 |
| **fix** | 버그 수정 |
| **docs** | 문서 수정 (README, docs 폴더 등) |
| **style** | 코드 포맷팅, 세미콜론 누락 (코드 변경 없음) |
| **refactor** | 코드 리팩토링 (기능 변경 없음) |
| **test** | 테스트 코드 추가/수정 |
| **chore** | 빌드 업무 수정, 패키지 매니저 설정 등 |

**Example:**
*   `feat: 소셜 로그인(카카오) 기능 구현`
*   `fix: 주문 금액 계산 오류 수정`
*   `docs: API 명세서 업데이트`
