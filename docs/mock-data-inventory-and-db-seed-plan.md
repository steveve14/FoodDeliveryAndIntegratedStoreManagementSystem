# 2026-03-17 Mock 데이터 인벤토리 및 DB 시드 반영 계획

## 1. 목적

- 프론트 소스 기준 현재 남아있는 mock/더미 데이터 위치를 식별
- 수동 DB 반영을 위해 `backend/msa-root/database` 시드 SQL에 데이터 확장 반영
- 유형별 5건 추가 기준으로 시드 데이터 보강

## 2. Mock 데이터 인벤토리 (소스 기준)

### 2.1 web-admin

- `frontend/web-admin/app/pages/system/versions.vue`
  - 2026-03-17 후속 작업으로 랜덤 mock 제거
  - `useAdminHomeSource` 주문 데이터를 기반으로 앱 버전 목록 파생 생성

- `frontend/web-admin/app/pages/operation/reviews.vue`
  - `// Mock Data 업데이트` 주석 문자열만 잔존
  - 데이터 소스 자체는 실데이터 기반(`useAdminHomeSource`)으로 전환됨

### 2.2 web-user

- `frontend/web-user/app/components/home/HomeSales.vue`
  - 2026-03-17 후속 작업으로 더미 메뉴/랜덤 생성 제거
  - `useHomeUserSource` 실주문 데이터 기반 최근 주문 표시

- `frontend/web-user/app/components/home/HomeStats.vue`
  - 2026-03-17 후속 작업으로 랜덤 수치 제거
  - `useHomeUserSource` 기반 기간 집계/이전 구간 증감률 계산

- `frontend/web-user/app/components/home/HomeChart.client.vue`
  - 2026-03-17 후속 작업으로 랜덤 매출 포인트 제거
  - `useHomeUserSource` 기반 기간 버킷(일/주/월) 매출 합계 차트로 전환

- `frontend/web-user/app/pages/checkout.vue`
  - 샘플 결제수단 안내 문구 존재

### 2.3 web-shop

- `frontend/web-shop/app/layouts/default.vue`
  - 2026-03-17 후속 작업으로 더미 배열 제거
  - `useHomeStoreSource` 기반 배달 대기 주문 목록으로 전환

## 3. DB 시드 반영 (유형별 5건 추가)

다음 5개 유형(도메인) 기준으로 각각 5건 추가 반영함.

1. User (`db_user`) 5건
2. Store (`db_store`) 5건
3. Order (`db_order`) 5건
4. Delivery (`db_delivery`) 5건
5. Event (`db_event`) 5건

## 4. 수정된 DB 파일

- `backend/msa-root/database/07_remote_db_user_seed.sql`
  - `user-seed-001` ~ `user-seed-005` 사용자 5건 추가
  - 재실행 안정성을 위해 `DELETE ... IN (...)` 대상에 동일 ID 추가

- `backend/msa-root/database/08_remote_db_store_seed.sql`
  - `store-seed-001` ~ `store-seed-005` 매장 5건 추가
  - `DELETE ... IN (...)` 대상 확장

- `backend/msa-root/database/09_remote_db_order_seed.sql`
  - `order-016` ~ `order-020` 주문 5건 추가
  - `order-item-024` ~ `order-item-028` 주문항목 5건 추가
  - `DELETE ... IN (...)` 대상 확장

- `backend/msa-root/database/10_remote_db_delivery_seed.sql`
  - `delivery-011` ~ `delivery-015` 배달 5건 추가
  - `DELETE ... IN (...)` 대상 확장

- `backend/msa-root/database/11_remote_db_event_seed.sql`
  - `event-016` ~ `event-020` 이벤트 5건 추가
  - `DELETE ... IN (...)` 대상 확장

## 5. 수동 반영 순서

기존과 동일하게 도메인별 DB에서 아래 파일을 순서대로 수동 실행:

1. `07_remote_db_user_seed.sql`
2. `08_remote_db_store_seed.sql`
3. `09_remote_db_order_seed.sql`
4. `10_remote_db_delivery_seed.sql`
5. `11_remote_db_event_seed.sql`

## 6. 참고

- 이번 작업은 mock 인벤토리 문서화 + DB 시드 확장에 집중함
- 프론트 주요 mock 제거 완료(운영/마케팅/시스템 화면 + web-shop 레이아웃 + web-user 홈 컴포넌트)
- 잔여는 샘플 안내 문구 수준(예: `web-user/app/pages/checkout.vue`) 또는 주석 정리 항목
