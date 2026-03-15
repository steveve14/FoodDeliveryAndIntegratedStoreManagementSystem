# web-user — 고객용 배달 웹앱

![Nuxt](https://img.shields.io/badge/Nuxt-4.2.2-00DC82?logo=nuxt)
![Nuxt UI](https://img.shields.io/badge/Nuxt%20UI-4.3.0-00DC82?logo=nuxt&labelColor=020420)

> 일반 고객을 위한 배달 주문 웹 애플리케이션입니다.

## 주요 기능

- 매장 검색 / 카테고리 필터 (한식, 중식, 치킨, 피자 등 15개 카테고리)
- 매장 상세 / 메뉴 조회
- 장바구니 / 체크아웃 / 주문 생성
- 주문 내역 / 실시간 상태 추적
- 즐겨찾기 / 쿠폰·혜택
- 리뷰 / 고객 지원
- USER 역할 기반 접근 제어

## 기술 스택

- **Framework**: Nuxt 4.2.2 (Vue 3)
- **UI**: @nuxt/ui 4.3.0 (Tailwind CSS)
- **State**: composable + cookie session (useOrdering, useAuth, useApi)
- **인증**: httpOnly 쿠키 기반, 자동 토큰 갱신
- **API**: Gateway(http://localhost:8000) 경유, Nitro 서버 프록시

## 개발 서버 (port 3200)

```bash
pnpm install
pnpm dev
```

## 빌드

```bash
pnpm build
pnpm preview
```

## E2E 테스트

```bash
pnpm exec playwright test
```

> Playwright E2E 14단계 주문 플로우 시나리오 통과 확인 (2026-03-13)

## 환경변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `NUXT_PUBLIC_API_BASE_URL` | `http://localhost:8000` | 백엔드 Gateway 주소 |
