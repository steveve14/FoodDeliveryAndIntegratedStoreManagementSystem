# 2026-03-10 customer 주문 E2E / 매장 DTO 보강

> 작성일: 2026-03-10
> 목적: web-customer의 실제 주문 흐름 연결 상태와 고객용 매장 응답 보강 내용을 기록한다.

## 1. 이번 작업 요약

- service-store의 StoreDto에 customer 화면용 표시 필드 추가
  - eta
  - reviewCount
  - deliveryFee
  - heroIcon
  - tags
  - bestseller
  - promo
- service-store가 메뉴/카테고리 기반으로 위 표시 필드를 계산해 반환하도록 수정
- web-customer의 useOrdering가 프론트 내부 임시 map 대신 백엔드 StoreDto 확장 필드를 우선 사용하도록 변경
- customer checkout, orders, stores 상세 흐름은 기존 실제 API 연동 상태 유지

## 2. 반영 파일

- backend/msa-root/service-store/src/main/java/com/example/store/dto/StoreDto.java
- backend/msa-root/service-store/src/main/java/com/example/store/service/StoreService.java
- frontend/web-customer/app/composables/useOrdering.ts
- docs/FRONTEND_API_LIST.md

## 3. 검증 결과

- service-store compileJava: 성공
- web-customer nuxi typecheck: 성공

## 4. 브라우저 E2E 확인 상태

- 이번 세션에서 localhost:8000 Gateway 연결을 확인했으나, 런타임 서버가 떠 있지 않아 실제 브라우저 주문 생성부터 주문내역 반영까지의 end-to-end 실행은 수행하지 못함
- 따라서 현재 상태는 구현/컴파일 검증 완료, 브라우저 런타임 검증 대기 상태

## 5. 서버 기동 후 확인 절차

1. Gateway와 auth, user, store, order 서비스를 기동한다.
2. web-customer를 실행한다.
3. 고객 계정으로 로그인한다.
4. 홈에서 매장 진입 후 메뉴를 장바구니에 담는다.
5. checkout에서 주문 확정을 수행한다.
6. orders 화면으로 이동해 방금 생성한 주문이 목록에 반영되는지 확인한다.
7. 홈/매장 상세에서 eta, tags, promo가 백엔드 응답값으로 렌더링되는지 확인한다.

## 6. 남은 리스크

- reviewCount는 아직 실제 리뷰 집계가 없어 0 또는 파생값 수준이다.
- deliveryFee, eta, tags, promo는 현재 DB 컬럼이 아니라 서비스 로직에서 계산된 값이다.
- 실제 운영 수준으로 가려면 고객용 전용 DTO와 리뷰/프로모션 집계 소스가 추가로 필요하다.