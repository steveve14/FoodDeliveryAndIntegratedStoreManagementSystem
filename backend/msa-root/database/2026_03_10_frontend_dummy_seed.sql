-- Remote frontend seed manifest
-- Datasource: jdbc:postgresql://192.168.0.25:6000/postgres
-- 이 파일은 IntelliJ 실행 순서를 안내하는 인덱스다.
-- 실제 실행은 아래 분리 스크립트를 각 데이터소스에서 순서대로 수행한다.

SELECT '1. postgres -> 01_remote_postgres_create_databases.sql' AS step;
SELECT '2. db_user -> 02_remote_db_user_schema.sql' AS step;
SELECT '3. db_store -> 03_remote_db_store_schema.sql' AS step;
SELECT '4. db_order -> 04_remote_db_order_schema.sql' AS step;
SELECT '5. db_delivery -> 05_remote_db_delivery_schema.sql' AS step;
SELECT '6. db_event -> 06_remote_db_event_schema.sql' AS step;
SELECT '7. db_user -> 07_remote_db_user_seed.sql' AS step;
SELECT '8. db_store -> 08_remote_db_store_seed.sql' AS step;
SELECT '9. db_order -> 09_remote_db_order_seed.sql' AS step;
SELECT '10. db_delivery -> 10_remote_db_delivery_seed.sql' AS step;
SELECT '11. db_event -> 11_remote_db_event_seed.sql' AS step;
SELECT '12. verify -> 12_remote_db_user_verify.sql ~ 16_remote_db_event_verify.sql' AS step;