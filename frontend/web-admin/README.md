# web-admin — 관리자 대시보드

![Nuxt](https://img.shields.io/badge/Nuxt-4.2.2-00DC82?logo=nuxt)
![Nuxt UI](https://img.shields.io/badge/Nuxt%20UI-4.3.0-00DC82?logo=nuxt&labelColor=020420)

> 시스템 관리자를 위한 웹 대시보드입니다. 사용자/매장/주문/배달/이벤트 관리 기능을 제공합니다.

## 주요 기능

- 대시보드 KPI 차트, 알림, 빠른 액션
- 사용자 관리 / 매장 관리 / 주문 관리
- 배달 추적 / 이벤트·쿠폰 관리
- 재무 분석 / 마케팅 / 시스템 설정
- ADMIN 역할 기반 접근 제어

## 기술 스택

- **Framework**: Nuxt 4.2.2 (Vue 3)
- **UI**: @nuxt/ui 4.3.0 (Tailwind CSS)
- **State**: composable (useApi, useAuth, useStoreApi, useOrderApi 등)
- **인증**: httpOnly 쿠키 기반, 자동 토큰 갱신
- **API**: Gateway(http://localhost:8000) 경유, Nitro 서버 프록시

## 개발 서버 (port 3000)

```bash
pnpm dev
```

## Production

Build the application for production:

```bash
pnpm build
```

Locally preview production build:

```bash
pnpm preview
```

Check out the [deployment documentation](https://nuxt.com/docs/getting-started/deployment) for more information.

## 환경변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `NUXT_PUBLIC_API_BASE_URL` | `http://localhost:8000` | 백엔드 Gateway 주소 |

## 관련 문서

- [BACKEND_INTEGRATION_GUIDE.md](BACKEND_INTEGRATION_GUIDE.md) — 백엔드 연동 가이드
