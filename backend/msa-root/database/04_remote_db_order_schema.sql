-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_order

DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS orders CASCADE;

CREATE TABLE orders (
    id              VARCHAR(100) PRIMARY KEY,
    user_id         VARCHAR(100),
    store_id        VARCHAR(100) NOT NULL,
    total_amount    INTEGER      NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    created_at      TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_item (
    id              VARCHAR(100) PRIMARY KEY,
    order_id        VARCHAR(100) NOT NULL,
    menu_id         VARCHAR(100) NOT NULL,
    quantity        INTEGER      NOT NULL,
    price_snapshot  BIGINT       NOT NULL,
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id)
);