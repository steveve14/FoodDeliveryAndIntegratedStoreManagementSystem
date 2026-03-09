-- ============================================================
-- service-auth: db_user 내 refresh_tokens 테이블
-- 대상 DB: db_user (PostgreSQL, port 6000)
-- 엔티티: RefreshToken
-- 주의: users/addresses 테이블은 service-user에서 관리
-- ============================================================

DROP TABLE IF EXISTS refresh_tokens CASCADE;

-- refresh_tokens 테이블
CREATE TABLE refresh_tokens (
    id          VARCHAR(100) PRIMARY KEY,
    user_id     VARCHAR(100) NOT NULL,
    token       TEXT NOT NULL,
    revoked     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    expires_at  TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
