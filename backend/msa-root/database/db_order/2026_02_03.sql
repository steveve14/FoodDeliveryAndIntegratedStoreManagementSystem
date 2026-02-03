CREATE SCHEMA IF NOT EXISTS db_order;

CREATE TABLE db_order.orders (
                                 id              VARCHAR(100) PRIMARY KEY,
                                 user_id         VARCHAR(100),
                                 store_id        VARCHAR(100) NOT NULL,
                                 total_amount    INTEGER NOT NULL,
                                 status          VARCHAR(20) NOT NULL,
                                 created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE db_order.order_item (
                                     id                  VARCHAR(100) PRIMARY KEY,
                                     order_id            VARCHAR(100) NOT NULL,
                                     menu_id             VARCHAR(100) NOT NULL,
                                     quantity            INTEGER NOT NULL,
                                     price_snapshot      INTEGER NOT NULL,
                                     CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES db_order.orders(id)
);