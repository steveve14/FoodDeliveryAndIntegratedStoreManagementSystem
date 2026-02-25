CREATE TABLE db_user.refresh_tokens (
                                        id          VARCHAR(100) PRIMARY KEY,        -- 🚨 BIGSERIAL에서 VARCHAR(100)으로 변경됨
                                        user_id     VARCHAR(100) NOT NULL,
                                        token       VARCHAR(512) NOT NULL,
                                        revoked     BOOLEAN DEFAULT FALSE,
                                        created_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                        expires_at  TIMESTAMPTZ NOT NULL,
                                        CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES db_user.users(id)
);

CREATE INDEX idx_refresh_tokens_user_id ON db_user.refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON db_user.refresh_tokens(token);