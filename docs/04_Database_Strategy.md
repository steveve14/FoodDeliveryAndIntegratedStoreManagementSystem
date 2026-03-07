# 04. 데이터베이스 전략 (Database Strategy)

> **최종 수정**: 2026-03-04

## 1. 개발 및 운영 환경 분리 전략
Spring Profiles 기능을 사용하여, 설정 파일 변경만으로 DB를 교체합니다.  
ORM으로 **Spring Data JDBC**를 사용하며, JPA/Hibernate는 사용하지 않습니다.

### 1.1. Local/Test 환경 (Profile: `local`)
*   **DBMS**: H2 in-memory
*   **장점**: 별도의 DB 서버 설치 불필요, 서비스 시작 시 SQL DDL/DML 스크립트 자동 실행.
*   **설정 (`application.yml` — default 또는 `local` 프로파일)**:
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
*   **DBMS**: PostgreSQL (포트 5432)
*   **장점**: 동시성 처리, 트랜잭션 안정성, 대용량 데이터 처리에 적합.
*   **설정 (`application-prod.yml`)**:
    ```yaml
    spring:
      datasource:
        url: jdbc:postgresql://db-server:5432/mydb
        driver-class-name: org.postgresql.Driver
        username: ${DB_USER}
        password: ${DB_PASSWORD}
      sql:
        init:
          mode: never
    ```

## 2. 데이터 접근 계층

| 항목 | 내용 |
|---|---|
| **ORM / 데이터 접근** | Spring Data JDBC (JPA 미사용) |
| **로컬 DB** | H2 in-memory |
| **운영 DB** | PostgreSQL 5432 |
| **스키마 관리** | SQL 스크립트 (`schema.sql`) 직접 작성 |

> `spring.jpa.*` 설정은 사용하지 않습니다. DDL 자동 생성(`ddl-auto`) 없이 SQL 스크립트로 테이블을 명시적으로 정의합니다.

## 3. 서비스별 DB 분리 (Database per Service)
각 마이크로서비스는 독립된 데이터베이스(또는 스키마)를 가집니다.

| 서비스 | DB 스키마 | 핵심 테이블 |
|---|---|---|
| service-user | db_user | users |
| service-auth | — | (user DB 참조, 별도 토큰 저장 없음) |
| service-store | db_store | stores, products (menus) |
| service-order | db_order | orders, order_items |
| service-delivery | db_delivery | deliveries |
| service-event | db_marketing | events |

## 4. 도메인별 데이터 모델 (간략)

### User (`service-user`)
*   `id`, `email`, `password`, `role`, `nickname`

### Store (`service-store`)
*   `Store`: `id`, `owner_id`, `name`, `address`, `status`
*   `Menu(Product)`: `id`, `store_id`, `name`, `price`, `image_url`
*   `Table`: `id`, `store_id`, `table_number`, `x_position`, `y_position` (매장 배치용)

### Order (`service-order`)
*   `Order`: `id`, `user_id`, `store_id`, `total_price`, `status` (PENDING, COOKING, DELIVERING...), `type` (DELIVERY, HALL)
*   `OrderItem`: `id`, `order_id`, `menu_id`, `quantity`, `options`

### Delivery (`service-delivery`)
*   `Delivery`: `id`, `order_id`, `rider_id`, `status`, `address`

---