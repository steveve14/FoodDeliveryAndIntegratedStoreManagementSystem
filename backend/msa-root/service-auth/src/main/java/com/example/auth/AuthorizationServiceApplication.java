package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * AuthorizationServiceApplication
 *
 * 역할:
 * - Spring Boot 애플리케이션의 진입점(main 클래스)입니다.
 * - @EnableDiscoveryClient로 Eureka 등 서비스 디스커버리 클라이언트 기능을 사용합니다.
 *
 * 실행:
 * - 애플리케이션을 실행하면 Spring 컨텍스트가 초기화되고 컨트롤러/서비스/레포지토리 등이 등록됩니다.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthorizationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServiceApplication.class, args);
    }
}