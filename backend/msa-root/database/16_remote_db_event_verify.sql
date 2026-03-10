-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_event

SELECT COUNT(*) AS events_count FROM events;
SELECT id, type, created_at FROM events ORDER BY created_at DESC;