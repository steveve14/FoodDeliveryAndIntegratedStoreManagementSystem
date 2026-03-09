-- db_event 테이블 생성 (엔티티 aligned: 2026-03-07)
-- 연결: jdbc:postgresql://localhost:6000/db_event
-- 사용 서비스: service-event (8030)
-- ※ 기존 db_marketing → db_event로 변경 (application.yml 기준)

DROP TABLE IF EXISTS events CASCADE;

-- events (엔티티: com.example.event.entity.Event)
CREATE TABLE events (
    id              VARCHAR(100) PRIMARY KEY,
    type            VARCHAR(50),
    payload         TEXT,
    created_at      TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP
);