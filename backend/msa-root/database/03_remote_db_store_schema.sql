-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_store

DROP TABLE IF EXISTS menu CASCADE;
DROP TABLE IF EXISTS store CASCADE;

CREATE TABLE store (
    id               VARCHAR(100) PRIMARY KEY,
    owner_id         VARCHAR(100) NOT NULL,
    name             VARCHAR(100) NOT NULL,
    address          VARCHAR(255) NOT NULL,
    phone            VARCHAR(20),
    category         VARCHAR(20)  NOT NULL,
    status           VARCHAR(20)  DEFAULT 'OPEN',
    latitude         DOUBLE PRECISION,
    longitude        DOUBLE PRECISION,
    min_order_amount BIGINT       DEFAULT 0,
    rating_avg       DOUBLE PRECISION DEFAULT 0.0,
    description      TEXT,
    opening_hours    VARCHAR(255),
    created_at       TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_store_owner ON store(owner_id);

CREATE TABLE menu (
    id              VARCHAR(100) PRIMARY KEY,
    store_id        VARCHAR(100) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    price           BIGINT       NOT NULL,
    available       BOOLEAN      DEFAULT TRUE,
    created_at      TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_store FOREIGN KEY (store_id) REFERENCES store(id)
);