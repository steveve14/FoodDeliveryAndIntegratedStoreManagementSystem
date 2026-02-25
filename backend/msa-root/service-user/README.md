# service-user

이 모듈은 사용자 관리(MSA)의 기본 기능을 제공합니다. PostgreSQL과 Spring Data JDBC를 사용하며 Flyway로 마이그레이션을 관리합니다.

설정 (환경 변수 권장)
- DB_URL: JDBC URL (예: jdbc:postgresql://localhost:5432/db_user)
- DB_USERNAME: 데이터베이스 사용자
- DB_PASSWORD: 데이터베이스 비밀번호

마이그레이션
- 서비스 시작 시 Flyway가 `classpath:db/migration` 경로의 SQL 파일을 실행합니다.
- 예: `V1__create_user_table.sql`이 기본 테이블을 생성합니다.

엔드포인트
- POST /api/users/register : 사용자 등록 (JSON 바디: email, password, name)
- GET  /api/users?email=... : 이메일로 사용자 조회

실행 (개발)

```bat
cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
.\gradlew.bat :service-user:bootRun
```
