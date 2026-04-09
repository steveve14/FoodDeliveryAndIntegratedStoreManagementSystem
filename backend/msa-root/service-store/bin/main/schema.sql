-- schema.sql — H2 인메모리 DB 테이블 초기화 (local 프로파일용)
-- Spring Data JDBC는 quoted lowercase 식별자를 사용하므로
-- H2에서도 반드시 따옴표(double-quote)로 소문자 이름을 지정해야 합니다.

CREATE TABLE IF NOT EXISTS "store" (
    "id"                VARCHAR(100) PRIMARY KEY,
    "name"              VARCHAR(100) NOT NULL,
    "address"           VARCHAR(255) NOT NULL,
    "phone"             VARCHAR(20),
    "category"          VARCHAR(20),
    "status"            VARCHAR(20) DEFAULT 'OPEN',
    "latitude"          DOUBLE PRECISION,
    "longitude"         DOUBLE PRECISION,
    "min_order_amount"  BIGINT DEFAULT 0,
    "rating_avg"        DOUBLE PRECISION DEFAULT 0.0,
    "description"       TEXT,
    "opening_hours"     VARCHAR(100),
    "owner_id"          VARCHAR(100) NOT NULL,
    "created_at"        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "menu" (
    "id"          VARCHAR(100) PRIMARY KEY,
    "store_id"    VARCHAR(100) NOT NULL,
    "name"        VARCHAR(100) NOT NULL,
    "description" TEXT,
    "price"       BIGINT NOT NULL,
    "available"   BOOLEAN DEFAULT TRUE,
    "image_url"   TEXT,
    "created_at"  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_store FOREIGN KEY ("store_id") REFERENCES "store"("id")
);
