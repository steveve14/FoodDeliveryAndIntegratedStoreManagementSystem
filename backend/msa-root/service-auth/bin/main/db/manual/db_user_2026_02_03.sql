-- Central db_user SQL copied for manual application (from database/db_user/2026_02_03.sql)
CREATE SCHEMA IF NOT EXISTS db_user;

CREATE TABLE db_user.users (
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

CREATE TABLE db_user.addresses (
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
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES db_user.users(id)
);

CREATE INDEX idx_users_roles ON db_user.users(roles);
