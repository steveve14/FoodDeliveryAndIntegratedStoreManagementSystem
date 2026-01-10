CREATE TABLE IF NOT EXISTS users (
                                     id VARCHAR(100) PRIMARY KEY, -- Application에서 UUID 생성 후 주입
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255),
    nickname VARCHAR(50) NOT NULL,
    role VARCHAR(20) DEFAULT 'CUSTOMER' NOT NULL,
    social_type VARCHAR(20) DEFAULT 'NONE',
    social_id VARCHAR(100),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
    );

CREATE TRIGGER update_users_modtime
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE PROCEDURE update_timestamp_column();

CREATE TABLE IF NOT EXISTS address (
                                       id VARCHAR(100) PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    address_name VARCHAR(255) NOT NULL,
    detail_address VARCHAR(255) NOT NULL,
    zip_code VARCHAR(10),
    is_main BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );