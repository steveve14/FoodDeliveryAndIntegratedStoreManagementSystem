@echo off
REM =============================================================
REM 전체 DB 초기화 스크립트 (Windows)
REM PostgreSQL 포트: 6000, 사용자: postgres
REM =============================================================

set PGHOST=localhost
set PGPORT=6000
set PGUSER=postgres

echo [1/6] 데이터베이스 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -f 2026_02_03_db_create.sql 2>nul

echo [2/6] db_user 테이블 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_user -f db_user\2026_02_03.sql

echo [3/6] db_store 테이블 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_store -f db_store\2026_02_03.sql

echo [4/6] db_order 테이블 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_order -f db_order\2026_02_03.sql

echo [5/6] db_delivery 테이블 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_delivery -f db_delivery\2026_02_03.sql

echo [6/6] db_event 테이블 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_event -f db_marketing\2026_02_03.sql

echo.
echo 완료! 테스트 계정을 추가하려면:
echo psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_user -c "INSERT INTO users (id, email, password_hash, name, phone, roles, provider, created_at) VALUES (gen_random_uuid()::text, 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '관리자', '010-0000-0001', 'ADMIN', 'local', NOW()), (gen_random_uuid()::text, 'store@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '가게사장', '010-0000-0002', 'STORE', 'local', NOW()), (gen_random_uuid()::text, 'customer@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '일반사용자', '010-0000-0003', 'USER', 'local', NOW());"
pause