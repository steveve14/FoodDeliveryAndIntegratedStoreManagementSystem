-- schema.sql — H2 인메모리 DB 테이블 초기화 (local 프로파일용)
-- Spring Boot가 embedded DB 감지 시 자동 실행 (spring.sql.init.mode=embedded)
-- Spring Data JDBC는 quoted lowercase 식별자를 사용하므로
-- H2에서도 반드시 따옴표(double-quote)로 소문자 이름을 지정해야 합니다.

CREATE TABLE IF NOT EXISTS "users" (
    "id"              VARCHAR(100) PRIMARY KEY,
    "email"           VARCHAR(255) UNIQUE NOT NULL,
    "password_hash"   VARCHAR(255),
    "name"            VARCHAR(50)  NOT NULL,
    "phone"           VARCHAR(20),
    "roles"           VARCHAR(20)  NOT NULL,
    "provider"        VARCHAR(20),
    "provider_id"     VARCHAR(255),
    "created_at"      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "addresses" (
    "id"              VARCHAR(100) PRIMARY KEY,
    "user_id"         VARCHAR(100) NOT NULL,
    "label"           VARCHAR(100),
    "line1"           VARCHAR(255) NOT NULL,
    "line2"           VARCHAR(255),
    "city"            VARCHAR(100),
    "state"           VARCHAR(100),
    "postal_code"     VARCHAR(10),
    "country"         VARCHAR(100),
    "primary_address" BOOLEAN DEFAULT FALSE,
    "created_at"      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_address_user FOREIGN KEY ("user_id") REFERENCES "users"("id")
);
