# Remote DB IntelliJ Runbook

대상 서버

- Host: 192.168.0.25
- Port: 6000
- Admin DB: postgres
- User DB: db_user
- Store DB: db_store
- Order DB: db_order
- Delivery DB: db_delivery
- Event DB: db_event
- Username: postgres

IntelliJ DataSource 권장 구성

1. postgres 데이터소스
   URL: jdbc:postgresql://192.168.0.25:6000/postgres
2. db_user 데이터소스
   URL: jdbc:postgresql://192.168.0.25:6000/db_user
3. db_store 데이터소스
   URL: jdbc:postgresql://192.168.0.25:6000/db_store
4. db_order 데이터소스
   URL: jdbc:postgresql://192.168.0.25:6000/db_order
5. db_delivery 데이터소스
   URL: jdbc:postgresql://192.168.0.25:6000/db_delivery
6. db_event 데이터소스
   URL: jdbc:postgresql://192.168.0.25:6000/db_event

실행 순서

1. postgres 데이터소스에서 01_remote_postgres_create_databases.sql 실행
2. db_user 데이터소스에서 02_remote_db_user_schema.sql 실행
3. db_store 데이터소스에서 03_remote_db_store_schema.sql 실행
4. db_order 데이터소스에서 04_remote_db_order_schema.sql 실행
5. db_delivery 데이터소스에서 05_remote_db_delivery_schema.sql 실행
6. db_event 데이터소스에서 06_remote_db_event_schema.sql 실행
7. db_user 데이터소스에서 07_remote_db_user_seed.sql 실행
8. db_store 데이터소스에서 08_remote_db_store_seed.sql 실행
9. db_order 데이터소스에서 09_remote_db_order_seed.sql 실행
10. db_delivery 데이터소스에서 10_remote_db_delivery_seed.sql 실행
11. db_event 데이터소스에서 11_remote_db_event_seed.sql 실행
12. 각 DB에서 12~16 verify 스크립트 실행

legacy 파일

- 기존 psql/localhost 기준 스크립트는 database/old 로 이동했다.
- old/2026_02_03_db_create.legacy.sql
- old/2026_03_10_frontend_dummy_seed.legacy.sql
- old/apply_all_databases.localhost.legacy.bat