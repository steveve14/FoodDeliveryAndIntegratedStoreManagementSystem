# 🛵 음식 배달 및 통합 매장 관리 시스템 (MSA Ver.)

![Java](https://img.shields.io/badge/Java-17-007396?style=flat&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-6DB33F?style=flat&logo=springboot)
![Nuxt.js](https://img.shields.io/badge/Nuxt.js-3.0-00C58E?style=flat&logo=nuxt)
![MSA](https://img.shields.io/badge/Architecture-MSA-orange?style=flat)
![MyBatis Version](https://img.shields.io/badge/MyBatis-3.5.13-blue?style=flat&logo=MyBatis)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791?style=flat&logo=postgresql)



> **배달 앱과 매장 내 POS 시스템을 하나로 통합한 차세대 관리 플랫폼 개발 프로젝트입니다.**  
> Spring Boot 기반의 MSA 아키텍처와 Nuxt.js 프론트엔드를 적용하여 확장성과 유지보수성을 극대화했습니다.

---

## 📅 프로젝트 진행 현황
- [x] **프로젝트 기획 및 설계** (R&R, 아키텍처 정의)
- [ ] **백엔드 인프라 구축** (Eureka, Gateway, Config)
- [ ] **마이크로서비스 개발** (Auth, Order, Store)
- [ ] **프론트엔드 개발** (Nuxt.js 웹/대시보드)
- [ ] **모바일 앱 개발** (Kotlin 안드로이드)

---

## 🛠 기술 스택 (Tech Stack)

### Backend (Spring Ecosystem)
*   **Framework**: Spring Boot 3.x
*   **Language**: Java 17+
*   **Architecture**: Microservices Architecture (MSA)
*   **Discovery**: Spring Cloud Netflix Eureka
*   **Gateway**: Spring Cloud Gateway
*   **Communication**: OpenFeign (Sync), Kafka (Async - 예정)
*   **Database**: 
    *   **Dev/Test**: SQLite (Spring Data JPA + Profile 활용)
    *   **Prod**: MySQL/MariaDB (전환 용이하도록 추상화)
*   **Security**: Spring Security + JWT

### Frontend (Web & Dashboard)
*   **Framework**: Nuxt.js 3 (Vue 3 Composition API)
*   **State Management**: Pinia
*   **Styling**: Tailwind CSS / SCSS
*   **Environment**: Node.js 18+

### Mobile (App)
*   **OS**: Android
*   **Language**: Kotlin
*   **Network**: Retrofit2 (Gateway 연동)

---

## 🏗 시스템 아키텍처 (System Architecture)

본 프로젝트는 도메인별로 독립된 서비스가 동작하는 **마이크로서비스 아키텍처**를 따릅니다.

```mermaid
graph TD
    User(Client / Mobile / Web) -->|HTTPS| Gateway(API Gateway)
    
    subgraph Infrastructure
        Eureka(Service Discovery)
        Config(Config Server)
    end
    
    Gateway --> Auth(Auth Service)
    Gateway --> Store(Store Service)
    Gateway --> Order(Order Service)
    Gateway --> Payment(Payment Service)
    
    Store <-->|Feign| Order
    Order <-->|Feign| Payment
    
    Auth -.-> DB_Auth[(SQLite/MySQL)]
    Store -.-> DB_Store[(SQLite/MySQL)]
    Order -.-> DB_Order[(SQLite/MySQL)]
