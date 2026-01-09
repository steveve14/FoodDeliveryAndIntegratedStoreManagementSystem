# 04. 데이터베이스 전략 (Database Strategy)

## 1. 개발 및 운영 환경 분리 전략
Spring Data JPA의 추상화 기술과 Spring Profiles 기능을 사용하여, 코드 수정 없이 설정 파일 변경만으로 DB를 교체합니다.

### 1.1. Local/Test 환경 (Profile: `local`)
*   **DBMS**: SQLite
*   **장점**: 별도의 DB 서버 설치 불필요, 파일 하나로 관리되어 이동성 용이.
*   **설정 (`application-local.yml`)**:
    ```yaml
    spring:
      datasource:
        url: jdbc:sqlite:mydb.sqlite
        driver-class-name: org.sqlite.JDBC
      jpa:
        database-platform: org.hibernate.community.dialect.SQLiteDialect
        hibernate:
          ddl-auto: update # 서버 시작 시 테이블 자동 생성
    ```

### 1.2. Production 환경 (Profile: `prod`)
*   **DBMS**: MySQL (또는 MariaDB/PostgreSQL)
*   **장점**: 동시성 처리, 트랜잭션 안정성, 대용량 데이터 처리에 적합.
*   **설정 (`application-prod.yml`)**:
    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://db-server:3306/mydb
        driver-class-name: com.mysql.cj.jdbc.Driver
      jpa:
        database-platform: org.hibernate.dialect.MySQLDialect
        hibernate:
          ddl-auto: validate # 스키마 변경 방지
    ```

## 2. 도메인별 데이터 모델 (간략)

### User (Auth Service)
*   `id`, `email`, `password`, `role`, `nickname`

### Store (Store Service)
*   `Store`: `id`, `owner_id`, `name`, `address`, `status`
*   `Menu`: `id`, `store_id`, `name`, `price`, `image_url`
*   `Table`: `id`, `store_id`, `table_number`, `x_position`, `y_position` (매장 배치용)

### Order (Order Service)
*   `Order`: `id`, `user_id`, `store_id`, `total_price`, `status` (PENDING, COOKING, DELIVERING...), `type` (DELIVERY, HALL)
*   `OrderItem`: `id`, `order_id`, `menu_id`, `quantity`, `options`
