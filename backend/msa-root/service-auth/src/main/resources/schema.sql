-- schema.sql — H2 인메모리 DB 테이블 초기화 (local 프로파일용)
-- Spring Boot가 embedded DB 감지 시 자동 실행 (spring.sql.init.mode=embedded)
-- Spring Data JDBC는 quoted lowercase 식별자를 사용하므로
-- H2에서도 반드시 따옴표(double-quote)로 소문자 이름을 지정해야 합니다.

CREATE TABLE IF NOT EXISTS "refresh_tokens" (
    "id"          VARCHAR(100) PRIMARY KEY,
    "user_id"     VARCHAR(100) NOT NULL,
    "token"       VARCHAR(512) NOT NULL,
    "revoked"     BOOLEAN      DEFAULT FALSE,
    "created_at"  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "expires_at"  TIMESTAMP WITH TIME ZONE NOT NULL
);
