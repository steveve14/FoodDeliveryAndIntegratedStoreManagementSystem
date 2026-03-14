package com.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Application entry point for the gateway service. */
@SpringBootApplication
public class GatewayServiceApplication {
  /** 애플리케이션 실행 진입점입니다. */
  public static void main(String[] args) {
    SpringApplication.run(GatewayServiceApplication.class, args);
  }
}
