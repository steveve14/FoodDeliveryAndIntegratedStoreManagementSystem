# web-shop — 사장님/매장 운영 대시보드

![Nuxt](https://img.shields.io/badge/Nuxt-4.2.2-00DC82?logo=nuxt)
![Nuxt UI](https://img.shields.io/badge/Nuxt%20UI-4.3.0-00DC82?logo=nuxt&labelColor=020420)

> 매장 사장님을 위한 웹 대시보드입니다. 주문 접수, 메뉴 관리, 배달 추적, 매출 분석 기능을 제공합니다.

## 주요 기능

- 주문 관리 (배달/매장 통합 주문 목록)
- 메뉴·상품 관리 (CRUD, 품절 처리)
- 배달 추적 및 관리
- 재무·매출 분석
- 마케팅·프로모션 관리
- STORE 역할 기반 접근 제어

## 기술 스택

- **Framework**: Nuxt 4.2.2 (Vue 3)
- **UI**: @nuxt/ui 4.3.0 (Tailwind CSS)
- **State**: composable (useApi, useAuth, useHomeStoreSource 등)
- **인증**: httpOnly 쿠키 기반, 자동 토큰 갱신
- **API**: Gateway(http://localhost:8000) 경유, Nitro 서버 프록시

## 개발 서버 (port 3100)

```bash
pnpm install
pnpm dev
```

## 빌드

```bash
pnpm build
pnpm preview
```

## 환경변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `NUXT_PUBLIC_API_BASE_URL` | `http://localhost:8000` | 백엔드 Gateway 주소 |
