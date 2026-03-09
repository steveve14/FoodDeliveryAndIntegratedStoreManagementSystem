-- ============================================================
-- service-delivery: db_delivery 초기화 스크립트
-- 대상 DB: db_delivery (PostgreSQL, port 6000)
-- 엔티티: Delivery
-- ============================================================

DROP TABLE IF EXISTS delivery CASCADE;

-- delivery 테이블
CREATE TABLE delivery (
    id           VARCHAR(100) PRIMARY KEY,
    order_id     VARCHAR(100) NOT NULL UNIQUE,
    courier      VARCHAR(100),
    status       VARCHAR(20) NOT NULL,
    delivery_fee INTEGER NOT NULL,
    scheduled_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_delivery_order_id ON delivery(order_id);
CREATE INDEX idx_delivery_status ON delivery(status);
