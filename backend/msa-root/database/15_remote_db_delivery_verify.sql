-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_delivery

SELECT COUNT(*) AS delivery_count FROM delivery;
SELECT * FROM delivery ORDER BY scheduled_at DESC;