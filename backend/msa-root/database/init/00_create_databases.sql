-- Docker Compose 초기화 스크립트 (1회 실행)
-- PostgreSQL 컨테이너 최초 기동 시 자동 실행됩니다.

-- 각 마이크로서비스 전용 데이터베이스 생성
CREATE DATABASE db_auth;
CREATE DATABASE db_user;
CREATE DATABASE db_store;
CREATE DATABASE db_order;
CREATE DATABASE db_delivery;
CREATE DATABASE db_event;
CREATE DATABASE db_gateway;
CREATE DATABASE db_discovery;
