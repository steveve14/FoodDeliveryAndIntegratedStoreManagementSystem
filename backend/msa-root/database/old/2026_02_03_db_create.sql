-- Remote PostgreSQL database bootstrap
-- Datasource: jdbc:postgresql://192.168.0.25:6000/postgres
-- IntelliJ Query Console에서 postgres 데이터소스에 연결한 뒤 1회 실행한다.
-- 이미 존재하는 데이터베이스가 있으면 개별 CREATE DATABASE 문만 실패할 수 있다.

CREATE DATABASE db_user;
CREATE DATABASE db_store;
CREATE DATABASE db_order;
CREATE DATABASE db_delivery;
CREATE DATABASE db_event;-- 개별 데이터베이스 생성
-- PostgreSQL 포트: 6000
-- psql -h localhost -p 6000 -U postgres -f 2026_02_03_db_create.sql

CREATE DATABASE db_user;
CREATE DATABASE db_store;
CREATE DATABASE db_order;
CREATE DATABASE db_delivery;
CREATE DATABASE db_event;