-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_user

DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS mail_messages CASCADE;
DROP TABLE IF EXISTS user_notifications CASCADE;
DROP TABLE IF EXISTS favorite_stores CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS addresses CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id            VARCHAR(100) PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    name          VARCHAR(50)  NOT NULL,
    phone         VARCHAR(20),
    roles         VARCHAR(20)  NOT NULL,
    provider      VARCHAR(20),
    provider_id   VARCHAR(255),
    username      VARCHAR(100),
    avatar_url    TEXT,
    marketing_status VARCHAR(30),
    location      VARCHAR(255),
    team_role     VARCHAR(30),
    created_at    TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_roles ON users(roles);
CREATE INDEX idx_users_username ON users(username);

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
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_addresses_user_id ON addresses(user_id);

CREATE TABLE cart_items (
    id         VARCHAR(100) PRIMARY KEY,
    user_id    VARCHAR(100) NOT NULL,
    store_id   VARCHAR(100) NOT NULL,
    store_name VARCHAR(255) NOT NULL,
    menu_id    VARCHAR(100) NOT NULL,
    menu_name  VARCHAR(255) NOT NULL,
    quantity   INTEGER      NOT NULL,
    price      INTEGER      NOT NULL,
    created_at TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_cart_items_user_id ON cart_items(user_id);
CREATE INDEX idx_cart_items_user_menu ON cart_items(user_id, store_id, menu_id);

CREATE TABLE favorite_stores (
    id            VARCHAR(100) PRIMARY KEY,
    user_id       VARCHAR(100) NOT NULL,
    store_id      VARCHAR(100) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    category      VARCHAR(100) NOT NULL,
    rating        DOUBLE PRECISION DEFAULT 0,
    delivery_time VARCHAR(100) NOT NULL,
    min_order     VARCHAR(100) NOT NULL,
    image_icon    VARCHAR(100) NOT NULL,
    created_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_favorite_stores_user_id ON favorite_stores(user_id);
CREATE INDEX idx_favorite_stores_user_store ON favorite_stores(user_id, store_id);

CREATE TABLE mail_messages (
    id            BIGINT PRIMARY KEY,
    from_user_id  VARCHAR(100) NOT NULL,
    subject       VARCHAR(255) NOT NULL,
    body          TEXT         NOT NULL,
    unread        BOOLEAN      DEFAULT FALSE,
    created_at    TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mail_from_user FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_mail_messages_from_user_id ON mail_messages(from_user_id);
CREATE INDEX idx_mail_messages_created_at ON mail_messages(created_at DESC);

CREATE TABLE user_notifications (
    id            BIGINT PRIMARY KEY,
    user_id       VARCHAR(100) NOT NULL,
    body          TEXT         NOT NULL,
    unread        BOOLEAN      DEFAULT FALSE,
    created_at    TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_notifications_user_id ON user_notifications(user_id);
CREATE INDEX idx_user_notifications_created_at ON user_notifications(created_at DESC);

CREATE TABLE refresh_tokens (
    id         VARCHAR(100) PRIMARY KEY,
    user_id    VARCHAR(100) NOT NULL,
    token      TEXT         NOT NULL,
    revoked    BOOLEAN      DEFAULT FALSE,
    created_at TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMPTZ  NOT NULL,
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);