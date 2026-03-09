-- ============================================================
-- service-event: db_event 초기화 스크립트
-- 대상 DB: db_event (PostgreSQL, port 6000)
-- 엔티티: Event
-- ============================================================

DROP TABLE IF EXISTS events CASCADE;

-- events 테이블
CREATE TABLE events (
    id         VARCHAR(100) PRIMARY KEY,
    type       VARCHAR(100) NOT NULL,
    payload    TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_events_type ON events(type);
