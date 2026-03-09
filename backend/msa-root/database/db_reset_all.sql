-- =============================================================================
-- 전체 데이터베이스 드롭 & 재생성 스크립트
-- 날짜: 2026-03-07
-- 설명: 모든 서비스 DB 테이블을 삭제하고 Java 엔티티와 일치하도록 재생성합니다.
--       PostgreSQL 포트는 6000번을 기준으로 합니다.
-- =============================================================================
--
-- ▶ 사용법 (psql):
--   1) 데이터베이스 먼저 생성 (최초 1회):
--      psql -h localhost -p 6000 -U postgres -f 00_create_databases.sql
--
--   2) 각 DB별 테이블 생성:
--      psql -h localhost -p 6000 -U postgres -d db_user     -f 01_db_user.sql
--      psql -h localhost -p 6000 -U postgres -d db_store    -f 02_db_store.sql
--      psql -h localhost -p 6000 -U postgres -d db_order    -f 03_db_order.sql
--      psql -h localhost -p 6000 -U postgres -d db_delivery -f 04_db_delivery.sql
--      psql -h localhost -p 6000 -U postgres -d db_event    -f 05_db_event.sql
--
--   3) 테스트 계정 삽입:
--      psql -h localhost -p 6000 -U postgres -d db_user     -f 06_seed_users.sql
--
--   ※ 또는 이 파일 하나로 전부 실행하려면:
--      psql -h localhost -p 6000 -U postgres -f db_reset_all.sql
--      (단, psql의 \c 명령어가 필요하므로 psql에서만 동작합니다)
-- =============================================================================


-- =============================================================================
-- [0] 데이터베이스 생성 (이미 존재하면 무시)
-- =============================================================================
-- ※ psql에서 실행 시: 아래 SELECT로 존재 여부 확인 후 수동 CREATE
-- ※ 이미 DB가 있으면 이 섹션은 건너뛰세요.

-- CREATE DATABASE db_user;
-- CREATE DATABASE db_store;
-- CREATE DATABASE db_order;
-- CREATE DATABASE db_delivery;
-- CREATE DATABASE db_event;


-- =============================================================================
-- [1] db_user (service-user + service-auth 공용)
-- =============================================================================
-- 연결: jdbc:postgresql://localhost:6000/db_user
-- 사용 서비스: service-user (포트 8010), service-auth (포트 7000)
-- =============================================================================

\c db_user

-- 테이블 드롭 (의존관계 역순)
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS addresses CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 인덱스 드롭 (테이블 삭제 시 자동 제거되지만 명시적으로)
DROP INDEX IF EXISTS idx_users_roles;
DROP INDEX IF EXISTS idx_refresh_tokens_user_id;
DROP INDEX IF EXISTS idx_refresh_tokens_token;

-- users 테이블 (엔티티: com.example.userservice.entity.User)
CREATE TABLE users (
    id              VARCHAR(100) PRIMARY KEY,
    email           VARCHAR(255) UNIQUE NOT NULL,
    password_hash   VARCHAR(255),
    name            VARCHAR(50)  NOT NULL,
    phone           VARCHAR(20),
    roles           VARCHAR(20)  NOT NULL,
    provider        VARCHAR(20),
    provider_id     VARCHAR(255),
    created_at      TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_roles ON users(roles);

-- addresses 테이블 (엔티티: com.example.userservice.entity.Address)
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
    primary_address BOOLEAN      DEFAULT FALSE,
    created_at      TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- refresh_tokens 테이블 (엔티티: com.example.auth.entity.RefreshToken)
CREATE TABLE refresh_tokens (
    id              VARCHAR(100) PRIMARY KEY,
    user_id         VARCHAR(100) NOT NULL,
    token           VARCHAR(512) NOT NULL,
    revoked         BOOLEAN      DEFAULT FALSE,
    created_at      TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMPTZ  NOT NULL,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token   ON refresh_tokens(token);


-- =============================================================================
-- [2] db_store (service-store)
-- =============================================================================
-- 연결: jdbc:postgresql://localhost:6000/db_store
-- 사용 서비스: service-store (포트 8020)
-- =============================================================================

\c db_store

DROP TABLE IF EXISTS menu CASCADE;
DROP TABLE IF EXISTS store CASCADE;
DROP INDEX IF EXISTS idx_store_owner;

-- store 테이블 (엔티티: com.example.store.entity.Store)
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

-- menu 테이블 (엔티티: com.example.store.entity.Menu)
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


-- =============================================================================
-- [3] db_order (service-order)
-- =============================================================================
-- 연결: jdbc:postgresql://localhost:6000/db_order
-- 사용 서비스: service-order (포트 8040)
-- =============================================================================

\c db_order

DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS orders CASCADE;

-- orders 테이블 (엔티티: com.example.order.entity.Order)
CREATE TABLE orders (
    id              VARCHAR(100) PRIMARY KEY,
    user_id         VARCHAR(100),
    store_id        VARCHAR(100) NOT NULL,
    total_amount    INTEGER      NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    created_at      TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP
);

-- order_item 테이블 (엔티티: com.example.order.entity.OrderItem)
CREATE TABLE order_item (
    id              VARCHAR(100) PRIMARY KEY,
    order_id        VARCHAR(100) NOT NULL,
    menu_id         VARCHAR(100) NOT NULL,
    quantity        INTEGER      NOT NULL,
    price_snapshot  BIGINT       NOT NULL,
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id)
);


-- =============================================================================
-- [4] db_delivery (service-delivery)
-- =============================================================================
-- 연결: jdbc:postgresql://localhost:6000/db_delivery
-- 사용 서비스: service-delivery (포트 8050)
-- =============================================================================

\c db_delivery

DROP TABLE IF EXISTS delivery CASCADE;

-- delivery 테이블 (엔티티: com.example.delivery.entity.Delivery)
CREATE TABLE delivery (
    id              VARCHAR(100) PRIMARY KEY,
    order_id        VARCHAR(100) NOT NULL UNIQUE,
    courier         VARCHAR(100),
    status          VARCHAR(20)  NOT NULL,
    delivery_fee    INTEGER      NOT NULL DEFAULT 0,
    scheduled_at    TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP
);


-- =============================================================================
-- [5] db_event (service-event)
-- =============================================================================
-- 연결: jdbc:postgresql://localhost:6000/db_event
-- 사용 서비스: service-event (포트 8030)
-- =============================================================================

\c db_event

DROP TABLE IF EXISTS events CASCADE;

-- events 테이블 (엔티티: com.example.event.entity.Event)
CREATE TABLE events (
    id              VARCHAR(100) PRIMARY KEY,
    type            VARCHAR(50),
    payload         TEXT,
    created_at      TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP
);


-- =============================================================================
-- [6] 테스트 계정 시드 데이터 (db_user)
-- =============================================================================
-- 비밀번호: 'password123' (BCrypt 해시)
-- =============================================================================

\c db_user

INSERT INTO users (id, email, password_hash, name, phone, roles, provider, created_at) VALUES
  (gen_random_uuid()::text, 'admin@example.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '관리자',     '010-0000-0001', 'ADMIN', 'local', NOW()),
  (gen_random_uuid()::text, 'store@example.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '가게사장',   '010-0000-0002', 'STORE', 'local', NOW()),
  (gen_random_uuid()::text, 'customer@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '일반사용자', '010-0000-0003', 'USER',  'local', NOW());


-- =============================================================================
-- 완료
-- =============================================================================
-- 모든 테이블이 Java 엔티티와 정확히 일치하도록 재생성되었습니다.
--
-- ┌─────────────────┬──────────────────────────────────┬───────────┐
-- │ 데이터베이스     │ 테이블                            │ 서비스     │
-- ├─────────────────┼──────────────────────────────────┼───────────┤
-- │ db_user         │ users, addresses, refresh_tokens │ user/auth │
-- │ db_store        │ store, menu                      │ store     │
-- │ db_order        │ orders, order_item               │ order     │
-- │ db_delivery     │ delivery                         │ delivery  │
-- │ db_event        │ events                           │ event     │
-- └─────────────────┴──────────────────────────────────┴───────────┘
--
-- 테스트 로그인:
--   ADMIN  → admin@example.com    / password123
--   STORE  → store@example.com    / password123
--   USER   → customer@example.com / password123
-- =============================================================================
