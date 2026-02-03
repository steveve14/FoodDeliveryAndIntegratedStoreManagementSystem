CREATE SCHEMA IF NOT EXISTS db_user;

CREATE TABLE db_user.users (
    id              VARCHAR(100) PRIMARY KEY,
    email           VARCHAR(255) UNIQUE NOT NULL,
    password        VARCHAR(255),
    nickname        VARCHAR(50) NOT NULL,
    phone_number    VARCHAR(20) NOT NULL,
    role            VARCHAR(20) NOT NULL,
    social_type     VARCHAR(20),
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE db_user.address (
    id              VARCHAR(100) PRIMARY KEY,
    user_id         VARCHAR(100) NOT NULL,
    address_name    VARCHAR(100),
    road_address    VARCHAR(255) NOT NULL,
    detail_address  VARCHAR(255) NOT NULL,
    zip_code        VARCHAR(10),
    latitude        DOUBLE PRECISION NOT NULL,
    longitude       DOUBLE PRECISION NOT NULL,
    is_main         BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES db_user.users(id)
);

CREATE INDEX idx_users_role ON db_user.users(role);