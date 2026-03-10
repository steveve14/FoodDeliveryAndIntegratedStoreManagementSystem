-- db_delivery 테이블 생성 (엔티티 aligned: 2026-03-07)
-- 연결: jdbc:postgresql://localhost:6000/db_delivery
-- 사용 서비스: service-delivery (8050)

DROP TABLE IF EXISTS delivery CASCADE;

-- delivery (엔티티: com.example.delivery.entity.Delivery)
CREATE TABLE delivery (
    id              VARCHAR(100) PRIMARY KEY,
    order_id        VARCHAR(100) NOT NULL UNIQUE,
    courier         VARCHAR(100),
    status          VARCHAR(20)  NOT NULL,
    delivery_fee    INTEGER      NOT NULL DEFAULT 0,
    scheduled_at    TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP
);