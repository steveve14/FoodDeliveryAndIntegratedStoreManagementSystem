CREATE SCHEMA IF NOT EXISTS db_delivery;

CREATE TABLE db_delivery.delivery (
                                      id              VARCHAR(100) PRIMARY KEY,
                                      order_id        VARCHAR(100) NOT NULL UNIQUE,
                                      rider_id        VARCHAR(100),
                                      status          VARCHAR(20) NOT NULL,
                                      delivery_fee    INTEGER NOT NULL,
                                      created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);