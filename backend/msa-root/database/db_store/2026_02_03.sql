CREATE SCHEMA IF NOT EXISTS db_store;

CREATE TABLE db_store.store (
                                id                  VARCHAR(100) PRIMARY KEY,
                                owner_id            VARCHAR(100) NOT NULL,
                                name                VARCHAR(100) NOT NULL,
                                category            VARCHAR(20) NOT NULL,
                                tel_number          VARCHAR(20),
                                address             VARCHAR(255) NOT NULL,
                                latitude            DOUBLE PRECISION NOT NULL,
                                longitude           DOUBLE PRECISION NOT NULL,
                                status              VARCHAR(20) DEFAULT 'OPEN',
                                min_order_amount    INTEGER DEFAULT 0,
                                rating_avg          DOUBLE PRECISION DEFAULT 0.0,
                                review_count        INTEGER DEFAULT 0,
                                created_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                updated_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE db_store.menu (
                               id              VARCHAR(100) PRIMARY KEY,
                               store_id        VARCHAR(100) NOT NULL,
                               category_name   VARCHAR(50) NOT NULL,
                               name            VARCHAR(100) NOT NULL,
                               price           INTEGER NOT NULL,
                               description     TEXT,
                               image_url       VARCHAR(255),
                               is_soldout      BOOLEAN DEFAULT FALSE,
                               CONSTRAINT fk_menu_store FOREIGN KEY (store_id) REFERENCES db_store.store(id)
);

-- 인덱스 생성
CREATE INDEX idx_store_owner ON db_store.store(owner_id);