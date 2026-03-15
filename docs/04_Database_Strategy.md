# 04. 데이터베이스 전략 (Database Strategy)

> **최종 수정**: 2026-03-15

## 1. 개발 및 운영 환경 분리 전략
Spring Profiles 기능을 사용하여, 설정 파일 변경만으로 DB를 교체합니다.  
ORM으로 **Spring Data JDBC**를 사용하며, JPA/Hibernate는 사용하지 않습니다.

### 1.1. Local/Test 환경 (Profile: `local`)
* **DBMS**: H2 in-memory
* **장점**: 별도의 DB 서버 설치 불필요, 서비스 시작 시 SQL DDL/DML 스크립트 자동 실행.
* **설정 (`application.yml` — default 또는 `local` 프로파일)**:
    ```yaml
    spring:
      datasource:
        url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
        driver-class-name: org.h2.Driver
      sql:
        init:
          mode: always
          schema-locations: classpath:schema.sql
          data-locations: classpath:data.sql
    ```

### 1.2. Production 환경 (Profile: `prod`)
* **DBMS**: PostgreSQL (포트 6000)
* **장점**: 동시성 처리, 트랜잭션 안정성, 대용량 데이터 처리에 적합.
* **설정 (`application-prod.yml`)**:
    ```yaml
    spring:
      datasource:
        url: jdbc:postgresql://db-server:6000/mydb
        driver-class-name: org.postgresql.Driver
        username: ${DB_USER}
        password: ${DB_PASSWORD}
      sql:
        init:
          mode: never
    ```

## 2. 데이터 접근 계층

| 항목 | 내용 |
| --- | --- |
| **ORM / 데이터 접근** | Spring Data JDBC (JPA 미사용) |
| **로컬 DB** | H2 in-memory |
| **운영 DB** | PostgreSQL 6000 |
| **스키마 관리** | SQL 스크립트 (`schema.sql`) 직접 작성 |

> `spring.jpa.*` 설정은 사용하지 않습니다. DDL 자동 생성(`ddl-auto`) 없이 SQL 스크립트로 테이블을 명시적으로 정의합니다.

## 3. 서비스별 DB 분리 (Database per Service)
각 마이크로서비스는 독립된 데이터베이스(또는 스키마)를 가집니다.

| 서비스 | DB 스키마 | 핵심 테이블 |
| --- | --- | --- |
| service-user | db_user | users, addresses, cart_items, favorite_stores, user_notifications, mail_messages |
| service-auth | db_user | refresh_tokens (user DB 공유) |
| service-store | db_store | store, menu |
| service-order | db_order | orders, order_item |
| service-delivery | db_delivery | delivery |
| service-event | db_event | events |

## 4. 도메인별 데이터 모델 (간략)

### User (`service-user`)

* `id`, `email`, `password_hash`, `name`, `phone`, `roles`, `provider`, `provider_id`, `username`, `avatar_url`, `marketing_status`, `location`, `team_role`, `created_at`
* 관련 테이블: `addresses`, `cart_items`, `favorite_stores`, `user_notifications`, `mail_messages`

### Auth (`service-auth`)

* `RefreshToken`: `id`, `user_id`, `token`, `created_at` (db_user 내 refresh_tokens 테이블)

### Store (`service-store`)

* `Store`: `id`, `name`, `address`, `phone`, `category`, `status`, `lat`, `lng`, `rating`, `owner_id`
* `Menu`: `id`, `store_id`, `name`, `description`, `price`, `available`

### Order (`service-order`)

* `Order`: `id`, `user_id`, `store_id`, `total_amount`, `status` (CREATED, COOKING, DELIVERING, DONE, CANCELLED), `created_at`
* `OrderItem`: `id`, `order_id`, `menu_id`, `quantity`, `price_snapshot`

### Delivery (`service-delivery`)

* `Delivery`: `id`, `order_id`, `courier`, `status`, `delivery_fee`, `scheduled_at`

### Event (`service-event`)

* `Event`: `id`, `type`, `payload`, `created_at`

---
