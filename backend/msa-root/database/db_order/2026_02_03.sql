-- db_order 테이블 생성 (엔티티 aligned: 2026-03-07)
-- 연결: jdbc:postgresql://localhost:6000/db_order
-- 사용 서비스: service-order (8040)

DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS orders CASCADE;

-- orders (엔티티: com.example.order.entity.Order)
CREATE TABLE orders (
    id              VARCHAR(100) PRIMARY KEY,
    user_id         VARCHAR(100),
    store_id        VARCHAR(100) NOT NULL,
    total_amount    INTEGER      NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    created_at      TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP
);

-- order_item (엔티티: com.example.order.entity.OrderItem)
CREATE TABLE order_item (
    id              VARCHAR(100) PRIMARY KEY,
    order_id        VARCHAR(100) NOT NULL,
    menu_id         VARCHAR(100) NOT NULL,
    quantity        INTEGER      NOT NULL,
    price_snapshot  BIGINT       NOT NULL,
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id)
);