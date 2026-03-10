@echo off
REM =============================================================
REM 전체 DB 초기화 스크립트 (Windows)
REM PostgreSQL 포트: 6000, 사용자: postgres
REM =============================================================

set PGHOST=localhost
set PGPORT=6000
set PGUSER=postgres

echo [1/7] 데이터베이스 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -f 2026_02_03_db_create.sql 2>nul

echo [2/7] db_user 테이블 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_user -f db_user\init.sql

echo [3/7] db_store 테이블 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_store -f db_store\2026_02_03.sql

echo [4/7] db_order 테이블 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_order -f db_order\2026_02_03.sql

echo [5/7] db_delivery 테이블 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_delivery -f db_delivery\2026_02_03.sql

echo [6/7] db_event 테이블 생성...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d db_event -f db_event\2026_02_03.sql

echo [7/7] 프론트 검증용 더미 데이터 적재...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -f 2026_03_10_frontend_dummy_seed.sql

echo.
echo 완료! 프론트 검증용 시드 데이터 파일:
echo   database\2026_03_10_frontend_dummy_seed.sql
pause