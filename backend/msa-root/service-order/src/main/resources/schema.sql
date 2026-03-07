-- schema.sql — H2 인메모리 DB 테이블 초기화 (local 프로파일용)
-- Spring Data JDBC는 quoted lowercase 식별자를 사용하므로
-- H2에서도 반드시 따옴표(double-quote)로 소문자 이름을 지정해야 합니다.

CREATE TABLE IF NOT EXISTS "orders" (
    "id"            VARCHAR(100) PRIMARY KEY,
    "user_id"       VARCHAR(100),
    "store_id"      VARCHAR(100) NOT NULL,
    "total_amount"  INTEGER NOT NULL,
    "status"        VARCHAR(20) NOT NULL,
    "created_at"    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "order_item" (
    "id"              VARCHAR(100) PRIMARY KEY,
    "order_id"        VARCHAR(100) NOT NULL,
    "menu_id"         VARCHAR(100) NOT NULL,
    "quantity"        INTEGER NOT NULL,
    "price_snapshot"  BIGINT NOT NULL,
    CONSTRAINT fk_item_order FOREIGN KEY ("order_id") REFERENCES "orders"("id")
);
