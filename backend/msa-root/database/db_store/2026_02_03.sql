-- db_store 테이블 생성 (엔티티 aligned: 2026-03-07)
-- 연결: jdbc:postgresql://localhost:6000/db_store
-- 사용 서비스: service-store (8020)

DROP TABLE IF EXISTS menu CASCADE;
DROP TABLE IF EXISTS store CASCADE;

-- store (엔티티: com.example.store.entity.Store)
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

-- menu (엔티티: com.example.store.entity.Menu)
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