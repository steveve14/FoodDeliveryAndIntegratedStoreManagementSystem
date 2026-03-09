-- ============================================================
-- service-store: db_store 초기화 스크립트
-- 대상 DB: db_store (PostgreSQL, port 6000)
-- 엔티티: Store, Menu
-- ============================================================

-- 테이블 삭제 (의존 순서)
DROP TABLE IF EXISTS menu CASCADE;
DROP TABLE IF EXISTS store CASCADE;

-- store 테이블
CREATE TABLE store (
    id               VARCHAR(100) PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    address          VARCHAR(255) NOT NULL,
    phone            VARCHAR(20),
    category         VARCHAR(20) NOT NULL,
    status           VARCHAR(20) DEFAULT 'OPEN',
    latitude         DOUBLE PRECISION,
    longitude        DOUBLE PRECISION,
    min_order_amount BIGINT DEFAULT 0,
    rating_avg       DOUBLE PRECISION DEFAULT 0.0,
    description      TEXT,
    opening_hours    VARCHAR(255),
    owner_id         VARCHAR(100) NOT NULL,
    created_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_store_owner_id ON store(owner_id);
CREATE INDEX idx_store_category ON store(category);

-- menu 테이블
CREATE TABLE menu (
    id          VARCHAR(100) PRIMARY KEY,
    store_id    VARCHAR(100) NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    price       BIGINT NOT NULL,
    available   BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_store FOREIGN KEY (store_id) REFERENCES store(id) ON DELETE CASCADE
);

CREATE INDEX idx_menu_store_id ON menu(store_id);
