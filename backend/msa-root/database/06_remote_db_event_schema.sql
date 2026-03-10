-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_event

DROP TABLE IF EXISTS events CASCADE;

CREATE TABLE events (
    id              VARCHAR(100) PRIMARY KEY,
    type            VARCHAR(50),
    payload         TEXT,
    created_at      TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP
);