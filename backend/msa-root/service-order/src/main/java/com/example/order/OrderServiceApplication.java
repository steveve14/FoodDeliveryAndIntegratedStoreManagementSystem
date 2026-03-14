package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** Application entry point for the order service. */
@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApplication {

  /** 애플리케이션 실행 진입점입니다. */
  public static void main(String[] args) {
    SpringApplication.run(OrderServiceApplication.class, args);
  }
}
