-- ============================================================
-- service-order: db_order 초기화 스크립트
-- 대상 DB: db_order (PostgreSQL, port 6000)
-- 엔티티: Order, OrderItem
-- ============================================================

-- 테이블 삭제 (의존 순서)
DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS orders CASCADE;

-- orders 테이블
CREATE TABLE orders (
    id           VARCHAR(100) PRIMARY KEY,
    user_id      VARCHAR(100),
    store_id     VARCHAR(100) NOT NULL,
    total_amount INTEGER NOT NULL,
    status       VARCHAR(20) NOT NULL,
    created_at   TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_store_id ON orders(store_id);
CREATE INDEX idx_orders_status ON orders(status);

-- order_item 테이블
CREATE TABLE order_item (
    id              VARCHAR(100) PRIMARY KEY,
    order_id        VARCHAR(100) NOT NULL,
    menu_id         VARCHAR(100) NOT NULL,
    quantity        INTEGER NOT NULL,
    price_snapshot  BIGINT NOT NULL,
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_item_order_id ON order_item(order_id);
