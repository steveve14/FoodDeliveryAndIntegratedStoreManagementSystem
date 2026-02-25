# Kafka 도입 계획 (예정)

기준일: 2026-02-25  
대상 시스템: FoodDeliveryAndIntegratedStoreManagementSystem

## 1. 도입 목적
- 서비스 간 결합도 감소 (동기 호출 의존 완화)
- 주문/배달/이벤트 도메인의 비동기 처리 표준화
- 장애 전파 최소화 및 확장성 확보

## 2. 도입 범위 (1차)
- Producer: `service-order`
- Consumer: `service-delivery`, `service-event`
- 1차 이벤트:
  - `OrderCreated`
  - `OrderStatusChanged`

## 3. 단계별 전략

### Phase 1. 병행 도입 (동기 + 비동기)
- 기존 REST 흐름 유지
- 주문 생성/상태변경 시 Kafka 이벤트 발행 추가
- Consumer는 후처리(알림/배차 준비/통계 집계)부터 시작

### Phase 2. 안정화
- 재시도/에러 처리/DLQ 운영
- 멱등 처리 보강
- 모니터링 대시보드(consumer lag, 실패 건수) 구성

### Phase 3. 점진 전환
- 안정화된 흐름부터 동기 호출 일부를 이벤트 기반으로 치환
- 서비스 경계/책임 재정리

## 4. 토픽 설계 (초안)
- `order.events.v1`
  - 이벤트: `OrderCreated`, `OrderStatusChanged`
  - Key: `orderId` (주문 단위 순서 보장)
- `delivery.events.v1`
  - 이벤트: `DeliveryAssigned`, `DeliveryPickedUp`, `DeliveryCompleted`
- `marketing.events.v1`
  - 이벤트: `CouponIssued`, `CouponRedeemed`
- 실패 처리용:
  - `order.events.v1.dlq`
  - `delivery.events.v1.dlq`

## 5. 이벤트 메시지 표준 (초안)

```json
{
  "eventId": "uuid",
  "eventType": "OrderStatusChanged",
  "eventVersion": "v1",
  "aggregateId": "orderId",
  "occurredAt": "2026-02-25T12:34:56Z",
  "traceId": "trace-id",
  "payload": {
    "orderId": 123,
    "status": "COOKING",
    "storeId": 10,
    "userId": 77
  }
}
```

## 6. 구현 원칙
- DB 트랜잭션 정합성: Outbox 패턴 우선 검토
- 소비자 멱등성: `eventId` 기반 중복 처리 방지
- 역직렬화 호환성: `eventVersion` 기반 하위 호환 유지
- 장애 대응: 재시도 + DLQ + 알람

## 7. 기술 항목 체크리스트
- [ ] Kafka 브로커 로컬/개발 환경 구성 (Docker Compose 등)
- [ ] 공통 이벤트 스키마 정의
- [ ] `service-order` Producer 추가
- [ ] `service-delivery` Consumer 추가
- [ ] `service-event` Consumer 추가
- [ ] DLQ 토픽/핸들러 구성
- [ ] 멱등 처리 저장소(예: processed_events) 설계
- [ ] 모니터링 지표 수집(consumer lag, 처리시간, 실패율)

## 8. 완료 기준 (DoD)
- 주문 생성/상태 변경 이벤트 발행 및 소비 정상 동작
- 소비 실패 이벤트 DLQ 적재 확인
- 재기동/중복 수신 시 멱등 처리 검증 완료
- 운영 관측 지표 확인 가능

## 9. 오픈 이슈
- `service-payment` 도메인 분리 시점
- 스키마 레지스트리 도입 여부
- 이벤트 저장 보관 기간 및 파티션 전략
