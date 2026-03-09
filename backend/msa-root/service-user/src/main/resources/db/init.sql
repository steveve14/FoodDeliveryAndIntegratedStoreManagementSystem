-- ============================================================
-- service-user: db_user 초기화 스크립트
-- 대상 DB: db_user (PostgreSQL, port 6000)
-- 엔티티: User, Address
-- ============================================================

-- 테이블 삭제 (의존 순서)
DROP TABLE IF EXISTS addresses CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- users 테이블
CREATE TABLE users (
    id              VARCHAR(100) PRIMARY KEY,
    email           VARCHAR(255) UNIQUE NOT NULL,
    password_hash   VARCHAR(255),
    name            VARCHAR(50) NOT NULL,
    phone           VARCHAR(20),
    roles           VARCHAR(20) NOT NULL,
    provider        VARCHAR(20),
    provider_id     VARCHAR(255),
    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_roles ON users(roles);

-- addresses 테이블
CREATE TABLE addresses (
    id              VARCHAR(100) PRIMARY KEY,
    user_id         VARCHAR(100) NOT NULL,
    label           VARCHAR(100),
    line1           VARCHAR(255) NOT NULL,
    line2           VARCHAR(255),
    city            VARCHAR(100),
    state           VARCHAR(100),
    postal_code     VARCHAR(10),
    country         VARCHAR(100),
    primary_address BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_addresses_user_id ON addresses(user_id);
