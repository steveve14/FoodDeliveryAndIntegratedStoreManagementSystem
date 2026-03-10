-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_delivery

DROP TABLE IF EXISTS delivery CASCADE;

CREATE TABLE delivery (
    id              VARCHAR(100) PRIMARY KEY,
    order_id        VARCHAR(100) NOT NULL UNIQUE,
    courier         VARCHAR(100),
    status          VARCHAR(20)  NOT NULL,
    delivery_fee    INTEGER      NOT NULL DEFAULT 0,
    scheduled_at    TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP
);