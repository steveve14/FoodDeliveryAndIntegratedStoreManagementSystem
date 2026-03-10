@echo off
REM =============================================================
REM Remote DB 초기화 스크립트 (Windows / psql 필요)
REM Host: 192.168.0.25, Port: 6000, User: postgres
REM =============================================================

set PGHOST=192.168.0.25
set PGPORT=6000
set PGUSER=postgres

echo [1/11] 데이터베이스 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d postgres -f 01_remote_postgres_create_databases.sql 2>nul

echo [2/11] db_user 스키마 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_user -f 02_remote_db_user_schema.sql

echo [3/11] db_store 스키마 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_store -f 03_remote_db_store_schema.sql

echo [4/11] db_order 스키마 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_order -f 04_remote_db_order_schema.sql

echo [5/11] db_delivery 스키마 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_delivery -f 05_remote_db_delivery_schema.sql

echo [6/11] db_event 스키마 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_event -f 06_remote_db_event_schema.sql

echo [7/11] db_user 시드 적재...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_user -f 07_remote_db_user_seed.sql

echo [8/11] db_store 시드 적재...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_store -f 08_remote_db_store_seed.sql

echo [9/11] db_order 시드 적재...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_order -f 09_remote_db_order_seed.sql

echo [10/11] db_delivery 시드 적재...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_delivery -f 10_remote_db_delivery_seed.sql

echo [11/11] db_event 시드 적재...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_event -f 11_remote_db_event_seed.sql

echo.
echo 완료. 검증은 12_remote_db_user_verify.sql ~ 16_remote_db_event_verify.sql 참고.
pause