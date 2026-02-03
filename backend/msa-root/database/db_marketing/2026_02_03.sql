CREATE SCHEMA IF NOT EXISTS db_marketing;

CREATE TABLE db_marketing.events (
                                     id              VARCHAR(100) PRIMARY KEY,
                                     title           VARCHAR(100) NOT NULL,
                                     discount_val    INTEGER NOT NULL,
                                     start_at        TIMESTAMPTZ NOT NULL,
                                     end_at          TIMESTAMPTZ NOT NULL
);